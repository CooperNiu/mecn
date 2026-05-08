// Sidebar 组件
// 支持折叠功能、响应式设计

import { useState } from 'react'
import { Link, useLocation } from 'react-router-dom'
import { 
  LayoutDashboard, 
  Network, 
  GitBranch, 
  Waves, 
  Database,
  Settings,
  HelpCircle,
  ChevronLeft,
  ChevronRight
} from 'lucide-react'
import { clsx } from 'clsx'

const navigation = [
  { name: '仪表盘', href: '/', icon: LayoutDashboard },
  { name: '网络分析', href: '/network', icon: Network },
  { name: '因果发现', href: '/causal', icon: GitBranch },
  { name: '涟漪模拟', href: '/ripple', icon: Waves },
  { name: '数据源', href: '/data', icon: Database },
]

interface SidebarProps {
  collapsed?: boolean
  onToggle?: () => void
}

export default function Sidebar({ collapsed: controlledCollapsed, onToggle }: SidebarProps) {
  const [internalCollapsed, setInternalCollapsed] = useState(false)
  const location = useLocation()

  // 使用受控或非受控模式
  const collapsed = controlledCollapsed ?? internalCollapsed
  const handleToggle = onToggle ?? (() => setInternalCollapsed(!internalCollapsed))

  return (
    <div 
      className={clsx(
        'flex flex-col bg-white dark:bg-slate-800 border-r border-slate-200 dark:border-slate-700',
        'transition-all duration-300 ease-in-out',
        collapsed ? 'w-[72px]' : 'w-64'
      )}
    >
      {/* Logo */}
      <div className={clsx(
        'flex items-center h-16 border-b border-slate-200 dark:border-slate-700',
        collapsed ? 'justify-center px-2' : 'px-6'
      )}>
        <div className="flex items-center justify-center w-10 h-10 bg-primary-600 rounded-lg flex-shrink-0">
          <Network className="w-6 h-6 text-white" />
        </div>
        {!collapsed && (
          <div className="ml-3 overflow-hidden">
            <h1 className="text-xl font-bold text-slate-900 dark:text-white whitespace-nowrap">MECN</h1>
            <p className="text-xs text-slate-500 dark:text-slate-400 whitespace-nowrap">宏观经济因果网络</p>
          </div>
        )}
      </div>

      {/* 折叠按钮 */}
      <button
        onClick={handleToggle}
        className={clsx(
          'absolute -right-3 top-20 w-6 h-6 bg-white dark:bg-slate-700 border border-slate-200 dark:border-slate-600',
          'rounded-full flex items-center justify-center shadow-sm',
          'hover:bg-slate-50 dark:hover:bg-slate-600 transition-colors z-10'
        )}
      >
        {collapsed ? (
          <ChevronRight className="w-3 h-3 text-slate-600 dark:text-slate-300" />
        ) : (
          <ChevronLeft className="w-3 h-3 text-slate-600 dark:text-slate-300" />
        )}
      </button>

      {/* 导航 */}
      <nav className="flex-1 px-3 py-6 space-y-1">
        {navigation.map((item) => {
          const isActive = location.pathname === item.href
          return (
            <Link
              key={item.name}
              to={item.href}
              className={clsx(
                'flex items-center rounded-lg transition-all duration-200',
                collapsed ? 'justify-center px-2 py-3' : 'px-4 py-3',
                isActive
                  ? 'bg-primary-50 dark:bg-primary-900/30 text-primary-700 dark:text-primary-400'
                  : 'text-slate-600 dark:text-slate-400 hover:bg-slate-50 dark:hover:bg-slate-700/50 hover:text-slate-900 dark:hover:text-white'
              )}
              title={collapsed ? item.name : undefined}
            >
              <item.icon className={clsx(
                'w-5 h-5 flex-shrink-0',
                isActive ? 'text-primary-600 dark:text-primary-400' : 'text-slate-400 dark:text-slate-500'
              )} />
              {!collapsed && (
                <span className="ml-3 whitespace-nowrap">{item.name}</span>
              )}
              {!collapsed && isActive && (
                <div className="ml-auto w-1.5 h-1.5 bg-primary-600 dark:bg-primary-400 rounded-full" />
              )}
            </Link>
          )
        })}
      </nav>

      {/* 底部导航 */}
      <div className={clsx(
        'px-3 py-4 border-t border-slate-200 dark:border-slate-700 space-y-1',
        collapsed && 'px-2'
      )}>
        <Link
          to="/settings"
          className={clsx(
            'flex items-center rounded-lg text-slate-600 dark:text-slate-400',
            'hover:bg-slate-50 dark:hover:bg-slate-700/50 hover:text-slate-900 dark:hover:text-white',
            'transition-colors duration-200',
            collapsed ? 'justify-center px-2 py-3' : 'px-4 py-3'
          )}
          title={collapsed ? '设置' : undefined}
        >
          <Settings className="w-5 h-5 text-slate-400 dark:text-slate-500 flex-shrink-0" />
          {!collapsed && <span className="ml-3">设置</span>}
        </Link>
        <Link
          to="/help"
          className={clsx(
            'flex items-center rounded-lg text-slate-600 dark:text-slate-400',
            'hover:bg-slate-50 dark:hover:bg-slate-700/50 hover:text-slate-900 dark:hover:text-white',
            'transition-colors duration-200',
            collapsed ? 'justify-center px-2 py-3' : 'px-4 py-3'
          )}
          title={collapsed ? '帮助' : undefined}
        >
          <HelpCircle className="w-5 h-5 text-slate-400 dark:text-slate-500 flex-shrink-0" />
          {!collapsed && <span className="ml-3">帮助</span>}
        </Link>
      </div>
    </div>
  )
}
