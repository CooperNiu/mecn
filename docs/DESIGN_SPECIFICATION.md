# MECN 设计规范文档

## 🎨 设计令牌系统

### 颜色令牌

```typescript
// 颜色系统
export const colors = {
  // 主色调 - 专业金融蓝
  primary: {
    50: '#eff6ff',
    100: '#dbeafe',
    200: '#bfdbfe',
    300: '#93c5fd',
    400: '#60a5fa',
    500: '#3b82f6',
    600: '#2563eb',
    700: '#1d4ed8',
    800: '#1e40af',
    900: '#1e3a8a',
  },
  
  // 中性色
  neutral: {
    50: '#f8fafc',
    100: '#f1f5f9',
    200: '#e2e8f0',
    300: '#cbd5e1',
    400: '#94a3b8',
    500: '#64748b',
    600: '#475569',
    700: '#334155',
    800: '#1e293b',
    900: '#0f172a',
  },
  
  // 语义色
  semantic: {
    success: '#10b981',
    successLight: '#d1fae5',
    warning: '#f59e0b',
    warningLight: '#fef3c7',
    error: '#ef4444',
    errorLight: '#fee2e2',
    info: '#3b82f6',
    infoLight: '#dbeafe',
  },
  
  // 背景色
  background: {
    primary: '#f8fafc',
    secondary: '#ffffff',
    tertiary: '#f1f5f9',
    dark: '#0f172a',
    darkSecondary: '#1e293b',
    darkTertiary: '#334155',
  },
  
  // 文本色
  text: {
    primary: '#0f172a',
    secondary: '#475569',
    tertiary: '#64748b',
    inverse: '#f8fafc',
    darkPrimary: '#f8fafc',
    darkSecondary: '#94a3b8',
    darkTertiary: '#64748b',
  },
  
  // 边框色
  border: {
    light: '#e2e8f0',
    medium: '#cbd5e1',
    dark: '#334155',
  },
}
```

### 间距令牌

```typescript
export const spacing = {
  0: '0px',
  1: '4px',
  2: '8px',
  3: '12px',
  4: '16px',
  5: '20px',
  6: '24px',
  8: '32px',
  10: '40px',
  12: '48px',
  16: '64px',
  20: '80px',
  24: '96px',
}
```

### 排版令牌

```typescript
export const typography = {
  fontSize: {
    xs: '12px',
    sm: '14px',
    base: '16px',
    lg: '18px',
    xl: '20px',
    '2xl': '24px',
    '3xl': '30px',
    '4xl': '36px',
  },
  fontWeight: {
    light: 300,
    normal: 400,
    medium: 500,
    semibold: 600,
    bold: 700,
  },
  lineHeight: {
    tight: 1.25,
    normal: 1.5,
    relaxed: 1.75,
  },
}
```

### 圆角令牌

```typescript
export const borderRadius = {
  none: '0px',
  sm: '4px',
  md: '8px',
  lg: '12px',
  xl: '16px',
  '2xl': '24px',
  full: '9999px',
}
```

### 阴影令牌

```typescript
export const boxShadow = {
  sm: '0 1px 2px 0 rgb(0 0 0 / 0.05)',
  md: '0 4px 6px -1px rgb(0 0 0 / 0.1), 0 2px 4px -2px rgb(0 0 0 / 0.1)',
  lg: '0 10px 15px -3px rgb(0 0 0 / 0.1), 0 4px 6px -4px rgb(0 0 0 / 0.1)',
  xl: '0 20px 25px -5px rgb(0 0 0 / 0.1), 0 8px 10px -6px rgb(0 0 0 / 0.1)',
}
```

---

## 📐 布局规范

### 页面布局

```typescript
export const layout = {
  // 侧边栏
  sidebar: {
    width: '280px',
    collapsedWidth: '72px',
    minWidth: '280px',
    maxWidth: '320px',
  },
  
  // 头部
  header: {
    height: '64px',
    padding: '0 24px',
  },
  
  // 内容区
  content: {
    padding: '24px',
    maxWidth: '1600px',
    minWidth: '320px',
  },
  
  // 网格系统
  grid: {
    columns: 12,
    gap: '24px',
    breakpoints: {
      sm: '640px',
      md: '768px',
      lg: '1024px',
      xl: '1280px',
      '2xl': '1536px',
    },
  },
}
```

### 间距系统

```typescript
export const spacingSystem = {
  // 组件间距
  componentGap: '16px',
  sectionGap: '24px',
  pageGap: '32px',
  
  // 内边距
  paddingXs: '4px',
  paddingSm: '8px',
  paddingMd: '16px',
  paddingLg: '24px',
  paddingXl: '32px',
  
  // 外边距
  marginXs: '4px',
  marginSm: '8px',
  marginMd: '16px',
  marginLg: '24px',
  marginXl: '32px',
}
```

---

## 🧩 组件规范

### 按钮组件 (Button)

```typescript
export const buttonVariants = {
  // 主要按钮
  primary: {
    base: 'bg-primary-600 text-white hover:bg-primary-700 active:bg-primary-800',
    disabled: 'bg-primary-300 cursor-not-allowed',
    loading: 'bg-primary-600 text-white cursor-wait',
  },
  
  // 次要按钮
  secondary: {
    base: 'bg-white text-primary-600 border border-primary-600 hover:bg-primary-50',
    disabled: 'bg-gray-50 text-gray-400 border-gray-200 cursor-not-allowed',
    loading: 'bg-white text-primary-600 border border-primary-600 cursor-wait',
  },
  
  // 幽灵按钮
  ghost: {
    base: 'bg-transparent text-primary-600 hover:bg-primary-50',
    disabled: 'bg-transparent text-gray-400 cursor-not-allowed',
    loading: 'bg-transparent text-primary-600 cursor-wait',
  },
  
  // 危险按钮
  danger: {
    base: 'bg-error text-white hover:bg-red-600',
    disabled: 'bg-red-300 cursor-not-allowed',
    loading: 'bg-error text-white cursor-wait',
  },
}

export const buttonSizes = {
  sm: 'px-3 py-1.5 text-sm',
  md: 'px-4 py-2 text-base',
  lg: 'px-6 py-3 text-lg',
  xl: 'px-8 py-4 text-xl',
}
```

### 卡片组件 (Card)

```typescript
export const cardStyles = {
  // 基础卡片
  base: 'bg-white rounded-xl shadow-sm border border-slate-200',
  
  // 悬停卡片
  hover: 'hover:shadow-md hover:border-slate-300 transition-all duration-200',
  
  // 可点击卡片
  clickable: 'cursor-pointer hover:shadow-md hover:border-slate-300 active:scale-[0.98] transition-all duration-200',
  
  // 卡片头部
  header: 'px-6 py-4 border-b border-slate-200',
  
  // 卡片内容
  content: 'p-6',
  
  // 卡片底部
  footer: 'px-6 py-4 border-t border-slate-200 bg-slate-50 rounded-b-xl',
}

// 深色模式卡片
export const cardStylesDark = {
  base: 'bg-slate-800 rounded-xl shadow-sm border border-slate-700',
  hover: 'hover:shadow-md hover:border-slate-600 transition-all duration-200',
  header: 'px-6 py-4 border-b border-slate-700',
  content: 'p-6',
  footer: 'px-6 py-4 border-t border-slate-700 bg-slate-900 rounded-b-xl',
}
```

### 表格组件 (Table)

```typescript
export const tableStyles = {
  // 表格容器
  container: 'overflow-x-auto rounded-xl border border-slate-200',
  
  // 表格
  table: 'min-w-full divide-y divide-slate-200',
  
  // 表头
  header: 'bg-slate-50',
  headerCell: 'px-6 py-3 text-left text-xs font-medium text-slate-500 uppercase tracking-wider',
  
  // 表格行
  row: 'bg-white hover:bg-slate-50 transition-colors',
  rowEven: 'bg-slate-50/50',
  
  // 表格单元格
  cell: 'px-6 py-4 whitespace-nowrap text-sm text-slate-900',
  
  // 斑马纹
  striped: 'even:bg-slate-50',
  
  // 选中行
  selected: 'bg-primary-50 border-l-4 border-primary-500',
}
```

### 统计卡片 (StatCard)

```typescript
export const statCardStyles = {
  // 基础样式
  base: 'bg-white rounded-xl shadow-sm border border-slate-200 p-6',
  
  // 图标容器
  iconContainer: 'p-3 rounded-full',
  iconBlue: 'bg-blue-100 text-blue-600',
  iconGreen: 'bg-green-100 text-green-600',
  iconYellow: 'bg-yellow-100 text-yellow-600',
  iconRed: 'bg-red-100 text-red-600',
  
  // 数值
  value: 'text-2xl font-bold text-slate-900',
  
  // 标签
  label: 'text-sm font-medium text-slate-600',
  
  // 趋势指示器
  trendUp: 'text-green-600',
  trendDown: 'text-red-600',
  trendNeutral: 'text-slate-500',
}
```

### 模态框 (Modal)

```typescript
export const modalStyles = {
  // 背景遮罩
  overlay: 'fixed inset-0 bg-black/50 backdrop-blur-sm z-50',
  
  // 模态框容器
  container: 'fixed inset-0 z-50 overflow-y-auto',
  
  // 模态框内容
  content: 'bg-white rounded-2xl shadow-xl transform transition-all',
  
  // 尺寸
  sizes: {
    sm: 'max-w-md',
    md: 'max-w-lg',
    lg: 'max-w-2xl',
    xl: 'max-w-4xl',
    full: 'max-w-6xl',
  },
  
  // 头部
  header: 'px-6 py-4 border-b border-slate-200',
  
  // 内容
  body: 'px-6 py-4',
  
  // 底部
  footer: 'px-6 py-4 border-t border-slate-200 flex justify-end gap-3',
}
```

---

## 🎭 主题系统

### 主题配置

```typescript
export interface Theme {
  name: 'light' | 'dark' | 'system'
  colors: {
    primary: string
    secondary: string
    background: string
    surface: string
    text: string
    textSecondary: string
    border: string
  }
}

export const themes: Record<string, Theme> = {
  light: {
    name: 'light',
    colors: {
      primary: '#3b82f6',
      secondary: '#64748b',
      background: '#f8fafc',
      surface: '#ffffff',
      text: '#0f172a',
      textSecondary: '#475569',
      border: '#e2e8f0',
    },
  },
  dark: {
    name: 'dark',
    colors: {
      primary: '#60a5fa',
      secondary: '#94a3b8',
      background: '#0f172a',
      surface: '#1e293b',
      text: '#f8fafc',
      textSecondary: '#94a3b8',
      border: '#334155',
    },
  },
}
```

### 主题切换实现

```typescript
// 主题上下文
import { createContext, useContext, useState, useEffect } from 'react'

type Theme = 'light' | 'dark' | 'system'

interface ThemeContextType {
  theme: Theme
  setTheme: (theme: Theme) => void
  resolvedTheme: 'light' | 'dark'
}

const ThemeContext = createContext<ThemeContextType | undefined>(undefined)

export function ThemeProvider({ children }: { children: React.ReactNode }) {
  const [theme, setTheme] = useState<Theme>('system')
  const [resolvedTheme, setResolvedTheme] = useState<'light' | 'dark'>('light')
  
  useEffect(() => {
    const mediaQuery = window.matchMedia('(prefers-color-scheme: dark)')
    
    const updateTheme = () => {
      if (theme === 'system') {
        setResolvedTheme(mediaQuery.matches ? 'dark' : 'light')
      } else {
        setResolvedTheme(theme)
      }
    }
    
    updateTheme()
    mediaQuery.addEventListener('change', updateTheme)
    
    return () => mediaQuery.removeEventListener('change', updateTheme)
  }, [theme])
  
  useEffect(() => {
    document.documentElement.classList.toggle('dark', resolvedTheme === 'dark')
  }, [resolvedTheme])
  
  return (
    <ThemeContext.Provider value={{ theme, setTheme, resolvedTheme }}>
      {children}
    </ThemeContext.Provider>
  )
}

export const useTheme = () => {
  const context = useContext(ThemeContext)
  if (!context) {
    throw new Error('useTheme must be used within a ThemeProvider')
  }
  return context
}
```

---

## 📱 响应式断点

```typescript
export const breakpoints = {
  sm: '640px',
  md: '768px',
  lg: '1024px',
  xl: '1280px',
  '2xl': '1536px',
}

// Tailwind CSS 断点配置
export const tailwindBreakpoints = {
  sm: '640px',
  md: '768px',
  lg: '1024px',
  xl: '1280px',
  '2xl': '1536px',
}
```

### 响应式行为

```typescript
export const responsiveBehavior = {
  // 侧边栏
  sidebar: {
    mobile: 'hidden', // 移动端隐藏
    tablet: 'collapsible', // 平板端可折叠
    desktop: 'visible', // 桌面端可见
  },
  
  // 卡片网格
  grid: {
    mobile: 'grid-cols-1', // 移动端单列
    tablet: 'grid-cols-2', // 平板端两列
    desktop: 'grid-cols-4', // 桌面端四列
  },
  
  // 表格
  table: {
    mobile: 'card-view', // 移动端卡片视图
    tablet: 'scrollable', // 平板端可滚动
    desktop: 'full-view', // 桌面端完整视图
  },
}
```

---

## 🎨 图标系统

### 图标规范

```typescript
export const iconSizes = {
  xs: '12px',
  sm: '16px',
  md: '20px',
  lg: '24px',
  xl: '32px',
}

export const iconColors = {
  primary: 'text-primary-600',
  secondary: 'text-slate-500',
  success: 'text-green-600',
  warning: 'text-yellow-600',
  error: 'text-red-600',
  info: 'text-blue-600',
}
```

### 图标使用指南

1. **一致性**: 同一功能使用相同图标
2. **可识别性**: 图标含义清晰，避免歧义
3. **大小适配**: 根据上下文选择合适大小
4. **颜色语义**: 使用颜色表达状态（成功、警告、错误）

---

## ✨ 动画规范

### 过渡动画

```typescript
export const transitions = {
  // 基础过渡
  base: 'transition-all duration-200 ease-in-out',
  
  // 快速过渡
  fast: 'transition-all duration-100 ease-in-out',
  
  // 慢速过渡
  slow: 'transition-all duration-300 ease-in-out',
  
  // 弹性过渡
  bounce: 'transition-all duration-300 ease-[cubic-bezier(0.68,-0.55,0.265,1.55)]',
}

// 悬停效果
export const hoverEffects = {
  // 轻微抬升
  lift: 'hover:-translate-y-0.5 hover:shadow-md',
  
  // 缩放
  scale: 'hover:scale-[1.02]',
  
  // 背景变化
  background: 'hover:bg-slate-50',
  
  // 边框变化
  border: 'hover:border-primary-300',
}
```

### 加载动画

```typescript
export const loadingAnimations = {
  // 旋转
  spin: 'animate-spin',
  
  // 脉冲
  pulse: 'animate-pulse',
  
  // 弹跳
  bounce: 'animate-bounce',
  
  // 渐入
  fadeIn: 'animate-fadeIn',
  
  // 滑入
  slideIn: 'animate-slideIn',
}
```

---

## 📏 间距指南

### 组件间距

```typescript
export const componentSpacing = {
  // 按钮间距
  buttonGroup: 'gap-2', // 8px
  
  // 卡片间距
  cardGrid: 'gap-6', // 24px
  
  // 表单元素间距
  formGroup: 'space-y-4', // 16px
  
  // 列表项间距
  listItems: 'space-y-2', // 8px
  
  // 区块间距
  section: 'space-y-8', // 32px
}
```

### 页面间距

```typescript
export const pageSpacing = {
  // 页面内边距
  pagePadding: 'p-6', // 24px
  
  // 页面顶部间距
  pageTop: 'pt-6', // 24px
  
  // 页面底部间距
  pageBottom: 'pb-6', // 24px
  
  // 内容区最大宽度
  contentMaxWidth: 'max-w-7xl', // 1280px
}
```

---

## 🎯 使用建议

1. **遵循设计令牌**: 使用预定义的颜色、间距、字体等令牌
2. **保持一致性**: 同类组件使用相同样式
3. **响应式优先**: 移动端优先的设计思路
4. **可访问性**: 确保足够的对比度和键盘导航支持
5. **性能优化**: 避免过度使用动画和阴影

---

*文档版本: 1.0*
*创建日期: 2026-05-08*
*基于: FinceptTerminal 设计模式*
