// 主题上下文和提供者
// 支持深色/浅色主题切换

import { createContext, useContext, useState, useEffect, ReactNode } from 'react'

// 主题类型
export type Theme = 'light' | 'dark' | 'system'

// 主题上下文类型
interface ThemeContextType {
  theme: Theme
  setTheme: (theme: Theme) => void
  resolvedTheme: 'light' | 'dark'
}

// 创建上下文
const ThemeContext = createContext<ThemeContextType | undefined>(undefined)

// 主题提供者组件
interface ThemeProviderProps {
  children: ReactNode
  defaultTheme?: Theme
}

export function ThemeProvider({ 
  children, 
  defaultTheme = 'system' 
}: ThemeProviderProps) {
  const [theme, setTheme] = useState<Theme>(() => {
    // 从本地存储读取主题偏好
    if (typeof window !== 'undefined') {
      const stored = localStorage.getItem('mecn-theme') as Theme
      return stored || defaultTheme
    }
    return defaultTheme
  })
  
  const [resolvedTheme, setResolvedTheme] = useState<'light' | 'dark'>('light')
  
  // 监听系统主题变化
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
  
  // 更新文档类名
  useEffect(() => {
    const root = document.documentElement
    root.classList.remove('light', 'dark')
    root.classList.add(resolvedTheme)
    
    // 保存主题偏好到本地存储
    localStorage.setItem('mecn-theme', theme)
  }, [theme, resolvedTheme])
  
  return (
    <ThemeContext.Provider value={{ theme, setTheme, resolvedTheme }}>
      {children}
    </ThemeContext.Provider>
  )
}

// 主题 Hook
export function useTheme() {
  const context = useContext(ThemeContext)
  if (!context) {
    throw new Error('useTheme must be used within a ThemeProvider')
  }
  return context
}

// 主题切换组件
export function ThemeToggle() {
  const { theme, setTheme } = useTheme()
  
  const themes: { value: Theme; label: string; icon: string }[] = [
    { value: 'light', label: '浅色', icon: '☀️' },
    { value: 'dark', label: '深色', icon: '🌙' },
    { value: 'system', label: '系统', icon: '💻' },
  ]
  
  return (
    <div className="flex items-center gap-2">
      {themes.map(({ value, label, icon }) => (
        <button
          key={value}
          onClick={() => setTheme(value)}
          className={`
            p-2 rounded-lg transition-all duration-200
            ${theme === value 
              ? 'bg-primary-100 text-primary-600 dark:bg-primary-900 dark:text-primary-400' 
              : 'text-slate-500 hover:text-slate-700 dark:text-slate-400 dark:hover:text-slate-200 hover:bg-slate-100 dark:hover:bg-slate-800'
            }
          `}
          title={label}
        >
          <span className="text-lg">{icon}</span>
        </button>
      ))}
    </div>
  )
}

export default ThemeProvider
