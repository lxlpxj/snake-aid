const { chromium } = require('playwright');
(async () => {
  const browser = await chromium.launch({ headless: true });
  const page = await browser.newPage({ viewport: { width: 390, height: 844 } });
  const errs = [];
  page.on('console', m => { if (m.type() === 'error' || m.type() === 'warn') errs.push(m.type()+':'+m.text().substring(0,150)); });
  page.on('pageerror', e => errs.push('PAGEERR:'+e.message));
  await page.goto('http://localhost:5173/#/pages/record/index', { waitUntil: 'networkidle', timeout: 15000 });
  await page.waitForTimeout(3000);
  await page.screenshot({ path: 'd:/大学/毕业设计/snake/record_test.png', fullPage: true });
  console.log('截图OK');
  
  const inputs = await page.$$('input');
  const tas = await page.$$('textarea');
  console.log('原生input:', inputs.length, 'textarea:', tas.length);
  
  const uniInputs = await page.$$('.uni-input-input, .uni-textarea-textarea, [class*="uni-input"]');
  console.log('uni-input相关:', uniInputs.length);
  
  for (let i = 0; i < Math.min(inputs.length, 3); i++) {
    const box = await inputs[i].boundingBox();
    console.log('input['+i+']:', JSON.stringify(box));
    try {
      await inputs[i].click();
      await page.waitForTimeout(200);
      await page.keyboard.type('hi', { delay: 30 });
      const v = await inputs[i].inputValue();
      console.log('  输入成功:', v);
    } catch(e) { console.log('  FAIL:', e.message); }
  }
  
  // 看看 mode 到底是什么
  const mode = await page.evaluate(() => {
    const root = document.querySelector('#app');
    return root ? root.innerHTML.substring(0, 500) : 'no root';
  });
  console.log('页面内容前500字:', mode);
  
  console.log('错误:', errs.slice(0, 10));
  await browser.close();
})();
