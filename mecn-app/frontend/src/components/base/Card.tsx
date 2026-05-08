// Card 组件
// 支持头部、内容、底部，以及悬停效果

import { HTMLAttributes, forwardRef, ReactNode } from 'react'
import { clsx } from 'clsx'

// Card 属性
export interface CardProps extends HTMLAttributes<HTMLDivElement> {
  hover?: boolean
  clickable?: boolean
  bordered?: boolean
  padded?: boolean
}

// Card 头部属性
export interface CardHeaderProps extends HTMLAttributes<HTMLDivElement> {
  title?: string
  subtitle?: string
  action?: ReactNode
}

// Card 内容属性
export interface CardContentProps extends HTMLAttributes<HTMLDivElement> {
  noPadding?: boolean
}

// Card 底部属性
export interface CardFooterProps extends HTMLAttributes<HTMLDivElement> {
  align?: 'left' | 'center' | 'right' | 'between'
}

// Card 主体
export const Card = forwardRef<HTMLDivElement, CardProps>(
  (
    {
      hover = false,
      clickable = false,
      bordered = true,
      padded = true,
      className,
      children,
      ...props
    },
    ref
  ) => {
    return (
      <div
        ref={ref}
        className={clsx(
          // 基础样式
          'bg-white dark:bg-slate-800 rounded-xl shadow-sm',
          
          // 边框
          bordered && 'border border-slate-200 dark:border-slate-700',
          
          // 内边距
          padded && 'p-6',
          
          // 悬停效果
          hover && 'hover:shadow-md hover:border-slate-300 dark:hover:border-slate-600 transition-all duration-200',
          
          // 可点击
          clickable && 'cursor-pointer hover:shadow-md hover:border-slate-300 dark:hover:border-slate-600 active:scale-[0.98] transition-all duration-200',
          
          // 自定义类名
          className
        )}
        {...props}
      >
        {children}
      </div>
    )
  }
)

Card.displayName = 'Card'

// Card 头部
export const CardHeader = forwardRef<HTMLDivElement, CardHeaderProps>(
  ({ title, subtitle, action, className, children, ...props }, ref) => {
    return (
      <div
        ref={ref}
        className={clsx(
          'px-6 py-4 border-b border-slate-200 dark:border-slate-700',
          className
        )}
        {...props}
      >
        <div className="flex items-center justify-between">
          <div>
            {title && (
              <h3 className="text-lg font-semibold text-slate-900 dark:text-white">
                {title}
              </h3>
            )}
            {subtitle && (
              <p className="mt-1 text-sm text-slate-500 dark:text-slate-400">
                {subtitle}
              </p>
            )}
          </div>
          {action && <div>{action}</div>}
        </div>
        {children}
      </div>
    )
  }
)

CardHeader.displayName = 'CardHeader'

// Card 内容
export const CardContent = forwardRef<HTMLDivElement, CardContentProps>(
  ({ noPadding = false, className, children, ...props }, ref) => {
    return (
      <div
        ref={ref}
        className={clsx(
          !noPadding && 'p-6',
          className
        )}
        {...props}
      >
        {children}
      </div>
    )
  }
)

CardContent.displayName = 'CardContent'

// Card 底部
export const CardFooter = forwardRef<HTMLDivElement, CardFooterProps>(
  ({ align = 'right', className, children, ...props }, ref) => {
    const alignStyles = {
      left: 'justify-start',
      center: 'justify-center',
      right: 'justify-end',
      between: 'justify-between',
    }
    
    return (
      <div
        ref={ref}
        className={clsx(
          'px-6 py-4 border-t border-slate-200 dark:border-slate-700 bg-slate-50 dark:bg-slate-900 rounded-b-xl',
          'flex items-center',
          alignStyles[align],
          className
        )}
        {...props}
      >
        {children}
      </div>
    )
  }
)

CardFooter.displayName = 'CardFooter'

export default Card
