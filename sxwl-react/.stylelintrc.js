module.exports = {
  root: true,
  customSyntax: 'postcss-scss',
  extends: [
    'stylelint-config-standard-scss',
    'stylelint-config-prettier'
  ],
  rules: {
    // ===== 颜色规范：强制使用 Token 变量，禁止硬编码 =====
    'color-hex-length': 'short', // 要求使用短格式：#fff 而非 #ffffff
    'color-named': 'never', // 禁止使用颜色名称：red/blue，强制使用十六进制或变量
    
    // ===== 字符串规范 =====
    'string-quotes': 'single', // 要求使用单引号
    
    // ===== 缩进规范 =====
    'indentation': 2, // 使用 2 空格缩进
    
    // ===== 注释规范 =====
    'comment-no-empty': true, // 禁止空注释
    'comment-whitespace-inside': 'always', // 注释内容前必须有空格
    
    // ===== 媒体查询规范 =====
    'at-rule-no-vendor-prefix': true, // 禁止 @ 规则的前缀
    
    // ===== 选择器规范：BEM 命名法 =====
    'selector-class-pattern': '^[sxwl][a-zA-Z0-9_-]*$', // BEM 命名法检查
    
    // ===== 声明规范 =====
    'declaration-block-no-duplicate-properties': true, // 禁止重复声明
    'declaration-no-important': null, // 禁用 !important 检查（允许使用）
    
    // ===== SCSS 特定规则 =====
    'scss/double-slash-comment-empty-line-before': 'always', // SCSS 注释前需要空行
    
    // ===== 打印规范 =====
    'at-rule-no-unknown': null, // 禁用未知 @ 规则检查（支持 SCSS 语法）
    
    // ===== 允许的特殊情况 =====
    'selector-pseudo-element-colon-notation': 'double', // 使用 :: 而非 :
    'linebreaks': 'unix' // Windows 统一使用 Unix 换行符
  },
  ignoreFiles: [
    'node_modules/**/*',
    'dist/**/*',
    'public/**/*'
  ],
  overrides: [
    {
      files: ['**/*.scss'],
      customSyntax: 'postcss-scss'
    }
  ]
};
