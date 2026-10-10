const { chromium } = require('playwright');
(async () => {
  const browser = await chromium.launch({ headless: true });
  const page = await browser.newPage({ viewport: { width: 390, height: 844 } });
  
  // 监听所有 console 错误
  page.on('console', msg => {
    if (msg.type() === 'error') console.log('CONSOLE ERROR:', msg.text());
  });
  page.on('pageerror', err => console.log('PAGE ERROR:', err.message));
  page.on('requestfailed', req => console.log('REQUEST FAIL:', req.url()));
  
  await page.goto('http://localhost:5173/#/pages/record/index', { waitUntil: 'networkidle', timeout: 10000 });
  await page.waitForTimeout(2000);
  
  // 截图
  await page.screenshot({ path: 'd:/大学/毕业设计/snake/record_page.png', fullPage: true });
  console.log('截图已保存');
  
  // 查页面上有哪些 input
  const inputs = await page.('input');
  console.log('找到 input 数量:', inputs.length);
  
  for (let i = 0; i < inputs.length; i++) {
    const box = await inputs[i].boundingBox();
    const disabled = await inputs[i].isDisabled();
    const visible = await inputs[i].isVisible();
    console.log('input[' + i + ']: box=' + JSON.stringify(box) + ' disabled=' + disabled + ' visible=' + visible);
    
    // 试试聚焦
    try {
      await inputs[i].click();
      await page.waitForTimeout(300);
      const active = await page.evaluate(() => document.activeElement && document.activeElement.tagName);
      console.log('  点击后 activeElement:', active);
      
      // 试试打字
      await page.keyboard.type('test123', { delay: 50 });
      const val = await inputs[i].inputValue();
      console.log('  输入后值:', val);
    } catch(e) {
      console.log('  点击/输入失败:', e.message);
    }
  }
  
  // 看看有没有遮罩层
  const overlays = await page.('[style*="position: fixed"], [style*="z-index"]');
  console.log('可能的遮罩元素:', overlays.length);
  
  // 页面 HTML 结构（前2000字符）
  const html = await page.content();
  console.log('页面 HTML 长度:', html.length);
  console.log('包含 <input:', html.includes('<input'));
  console.log('包含 snakeName:', html.includes('snakeName'));
  console.log('包含 mode:', html.includes('mode'));
  
  await browser.close();
})();
