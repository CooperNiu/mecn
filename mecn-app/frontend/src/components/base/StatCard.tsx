// StatCard 组件
// 支持趋势展示、对比色、微动画

import { ReactNode } from 'react'
import { clsx } from 'clsx'
import { ArrowUpRight, ArrowDownRight, Minus } from 'lucide-react'

// 趋势类型
export type TrendType = 'up' | 'down' | 'neutral'

// 颜色类型
export type StatColor = 'primary' | 'success' | 'warning' | 'error' | 'info'

// StatCard 属性
export interface StatCardProps {
  title: string
  value: string | number
  trend?: TrendType
  change?: string
  changeLabel?: string
  icon?: ReactNode
  color?: StatColor
  description?: string
  loading?: boolean
  className?: string
  onClick?: () => void
}

// 颜色样式映射
const colorStyles: Record<StatColor, { bg: string; text: string; icon: string }> = {
  primary: {
    bg: 'bg-primary-100 dark:bg-primary-900/30',
    text: 'text-primary-600 dark:text-primary-400',
    icon: 'text-primary-600 dark:text-primary-400',
  },
  success: {
    bg: 'bg-green-100 dark:bg-green-900/30',
    text: 'text-green-600 dark:text-green-400',
    icon: 'text-green-600 dark:text-green-400',
  },
  warning: {
    bg: 'bg-yellow-100 dark:bg-yellow-900/30',
    text: 'text-yellow-600 dark:text-yellow-400',
    icon: 'text-yellow-600 dark:text-yellow-400',
  },
  error: {
    bg: 'bg-red-100 dark:bg-red-900/30',
    text: 'text-red-600 dark:text-red-400',
    icon: 'text-red-600 dark:text-red-400',
  },
  info: {
    bg: 'bg-blue-100 dark:bg-blue-900/30',
    text: 'text-blue-600 dark:text-blue-400',
    icon: 'text-blue-600 dark:text-blue-400',
  },
}

// 趋势图标
function TrendIcon({ trend }: { trend: TrendType }) {
  const iconClass = clsx(
    'w-4 h-4',
    trend === 'up' && 'text-green-500',
    trend === 'down' && 'text-red-500',
    trend === 'neutral' && 'text-slate-400'
  )

  if (trend === 'up') return <ArrowUpRight className={iconClass} />
  if (trend === 'down') return <ArrowDownRight className={iconClass} />
  return <Minus className={iconClass} />
}

// StatCard 组件
export function StatCard({
  title,
  value,
  trend = 'neutral',
  change,
  changeLabel = '较上月',
  icon,
  color = 'primary',
  description,
  loading = false,
  className,
  onClick,
}: StatCardProps) {
  const colors = colorStyles[color]

  if (loading) {
    return (
      <div className={clsx(
        'bg-white dark:bg-slate-800 rounded-xl shadow-sm border border-slate-200 dark:border-slate-700 p-6',
        className
      )}>
        <div className="animate-pulse">
          <div className="flex items-center justify-between">
            <div className="space-y-2">
              <div className="h-4 bg-slate-200 dark:bg-slate-700 rounded w-20" />
              <div className="h-8 bg-slate-200 dark:bg-slate-700 rounded w-24" />
            </div>
            <div className="w-12 h-12 bg-slate-200 dark:bg-slate-700 rounded-xl" />
          </div>
        </div>
      </div>
    )
  }

  return (
    <div
      className={clsx(
        'bg-white dark:bg-slate-800 rounded-xl shadow-sm border border-slate-200 dark:border-slate-700 p-6',
        'transition-all duration-200',
        onClick && 'cursor-pointer hover:shadow-md hover:border-slate-300 dark:hover:border-slate-600',
        className
      )}
      onClick={onClick}
    >
      <div className="flex items-start justify-between">
        {/* 内容区 */}
        <div className="flex-1">
          <p className="text-sm font-medium text-slate-600 dark:text-slate-400">{title}</p>
          <p className="mt-2 text-3xl font-bold text-slate-900 dark:text-white">{value}</p>
          
          {/* 趋势和变化 */}
          {change && (
            <div className="mt-3 flex items-center gap-1">
              <TrendIcon trend={trend} />
              <span className={clsx(
                'text-sm font-medium',
                trend === 'up' && 'text-green-600 dark:text-green-400',
                trend === 'down' && 'text-red-600 dark:text-red-400',
                trend === 'neutral' && 'text-slate-500 dark:text-slate-400'
              )}>
                {change}
              </span>
              <span className="text-sm text-slate-500 dark:text-slate-400 ml-1">{changeLabel}</span>
            </div>
          )}

          {/* 描述 */}
          {description && (
            <p className="mt-2 text-sm text-slate-500 dark:text-slate-400">{description}</p>
          )}
        </div>

        {/* 图标 */}
        {icon && (
          <div className={clsx('p-3 rounded-xl', colors.bg)}>
            <div className={clsx('w-6 h-6', colors.icon)}>
              {icon}
            </div>
          </div>
        )}
      </div>
    </div>
  )
}

// StatCard 网格容器
export interface StatCardGridProps {
  children: ReactNode
  columns?: 2 | 3 | 4
  className?: string
}

export function StatCardGrid({ children, columns = 4, className }: StatCardGridProps) {
  const gridStyles = {
    2: 'grid-cols-1 md:grid-cols-2',
    3: 'grid-cols-1 md:grid-cols-2 lg:grid-cols-3',
    4: 'grid-cols-1 md:grid-cols-2 lg:grid-cols-4',
  }

  return (
    <div className={clsx('grid gap-6', gridStyles[columns], className)}>
      {children}
    </div>
  )
}

export default StatCard
