// snake-proxy.js —— 一个文件搞定：前端静态 + /api 反代到后端
const http = require("http");
const https = require("https");
const fs = require("fs");
const path = require("path");
const { URL } = require("url");

const STATIC_DIR = path.join(__dirname, "snake-aid-app", "dist", "build", "h5");
const BACKEND = process.env.BACKEND || "http://127.0.0.1:8080";
const PORT = process.env.PORT || 3000;

const MIME = {
  ".html": "text/html; charset=utf-8",
  ".js":   "application/javascript; charset=utf-8",
  ".css":  "text/css; charset=utf-8",
  ".json": "application/json; charset=utf-8",
  ".png":  "image/png",
  ".jpg":  "image/jpeg",
  ".jpeg": "image/jpeg",
  ".gif":  "image/gif",
  ".svg":  "image/svg+xml",
  ".ico":  "image/x-icon",
  ".woff": "font/woff",
  ".woff2":"font/woff2",
  ".ttf":  "font/ttf",
  ".webp": "image/webp"
};

function proxyApi(req, res) {
  const target = new URL(req.url, BACKEND);
  const options = {
    hostname: target.hostname,
    port: target.port || 80,
    path: target.pathname + target.search,
    method: req.method,
    headers: { ...req.headers, host: target.host, "x-forwarded-for": req.socket.remoteAddress }
  };
  const mod = target.protocol === "https:" ? https : http;
  const proxyReq = mod.request(options, (proxyRes) => {
    res.writeHead(proxyRes.statusCode, proxyRes.headers);
    proxyRes.pipe(res);
  });
  proxyReq.on("error", (e) => {
    res.writeHead(502, { "Content-Type": "text/plain; charset=utf-8" });
    res.end("后端连接失败: " + e.message + "\n请确认后端在 " + BACKEND + " 运行中");
  });
  req.pipe(proxyReq);
}

function serveStatic(req, res) {
  let filePath = decodeURIComponent(req.url.split("?")[0]);
  if (filePath === "/") filePath = "/index.html";
  filePath = path.join(STATIC_DIR, filePath);

  // 防止目录穿越
  if (!filePath.startsWith(STATIC_DIR)) { res.writeHead(403); res.end("Forbidden"); return; }

  fs.stat(filePath, (err, stat) => {
    if (err || !stat.isFile()) {
      // SPA fallback
      fs.readFile(path.join(STATIC_DIR, "index.html"), (e, data) => {
        if (e) { res.writeHead(404); res.end("Not Found"); return; }
        res.writeHead(200, { "Content-Type": MIME[".html"] });
        res.end(data);
      });
      return;
    }
    const ext = path.extname(filePath).toLowerCase();
    res.writeHead(200, { "Content-Type": MIME[ext] || "application/octet-stream" });
    fs.createReadStream(filePath).pipe(res);
  });
}

http.createServer((req, res) => {
  console.log(new Date().toISOString(), req.method, req.url);
  if (req.url.startsWith("/api/") || req.url.startsWith("/v3/api-docs") || req.url.includes("swagger") || req.url.includes("knife4j")) {
    proxyApi(req, res);
  } else {
    serveStatic(req, res);
  }
}).listen(PORT, "0.0.0.0", () => {
  console.log("=========================================");
  console.log("🐍 蛇伤急救 全栈服务器启动!");
  console.log("   前端静态: " + STATIC_DIR);
  console.log("   后端反代: " + BACKEND);
  console.log("   监听端口: " + PORT);
  console.log("=========================================");
});
