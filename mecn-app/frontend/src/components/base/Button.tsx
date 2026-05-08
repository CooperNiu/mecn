// Button 组件
// 支持多种变体、尺寸和状态

import { ButtonHTMLAttributes, forwardRef, ReactNode } from 'react'
import { clsx } from 'clsx'

// 按钮变体
export type ButtonVariant = 'primary' | 'secondary' | 'ghost' | 'danger' | 'success'

// 按钮尺寸
export type ButtonSize = 'sm' | 'md' | 'lg' | 'xl'

// 按钮属性
export interface ButtonProps extends ButtonHTMLAttributes<HTMLButtonElement> {
  variant?: ButtonVariant
  size?: ButtonSize
  isLoading?: boolean
  leftIcon?: ReactNode
  rightIcon?: ReactNode
  fullWidth?: boolean
}

// 变体样式映射
const variantStyles: Record<ButtonVariant, string> = {
  primary: 'bg-primary-600 text-white hover:bg-primary-700 active:bg-primary-800 focus:ring-primary-500',
  secondary: 'bg-white text-primary-600 border border-primary-600 hover:bg-primary-50 active:bg-primary-100 focus:ring-primary-500',
  ghost: 'bg-transparent text-primary-600 hover:bg-primary-50 active:bg-primary-100 focus:ring-primary-500',
  danger: 'bg-error text-white hover:bg-red-600 active:bg-red-700 focus:ring-red-500',
  success: 'bg-semantic-success text-white hover:bg-green-600 active:bg-green-700 focus:ring-green-500',
}

// 尺寸样式映射
const sizeStyles: Record<ButtonSize, string> = {
  sm: 'px-3 py-1.5 text-sm',
  md: 'px-4 py-2 text-base',
  lg: 'px-6 py-3 text-lg',
  xl: 'px-8 py-4 text-xl',
}

// 禁用样式
const disabledStyles = 'opacity-50 cursor-not-allowed pointer-events-none'

// 加载样式
const loadingStyles = 'relative cursor-wait'

// Button 组件
export const Button = forwardRef<HTMLButtonElement, ButtonProps>(
  (
    {
      variant = 'primary',
      size = 'md',
      isLoading = false,
      leftIcon,
      rightIcon,
      fullWidth = false,
      disabled,
      className,
      children,
      ...props
    },
    ref
  ) => {
    const isDisabled = disabled || isLoading
    
    return (
      <button
        ref={ref}
        disabled={isDisabled}
        className={clsx(
          // 基础样式
          'inline-flex items-center justify-center font-medium rounded-lg',
          'focus:outline-none focus:ring-2 focus:ring-offset-2',
          'transition-all duration-200',
          
          // 变体样式
          variantStyles[variant],
          
          // 尺寸样式
          sizeStyles[size],
          
          // 状态样式
          isDisabled && disabledStyles,
          isLoading && loadingStyles,
          
          // 全宽
          fullWidth && 'w-full',
          
          // 自定义类名
          className
        )}
        {...props}
      >
        {/* 加载指示器 */}
        {isLoading && (
          <span className="absolute inset-0 flex items-center justify-center">
            <svg
              className="animate-spin h-5 w-5 text-current"
              xmlns="http://www.w3.org/2000/svg"
              fill="none"
              viewBox="0 0 24 24"
            >
              <circle
                className="opacity-25"
                cx="12"
                cy="12"
                r="10"
                stroke="currentColor"
                strokeWidth="4"
              />
              <path
                className="opacity-75"
                fill="currentColor"
                d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"
              />
            </svg>
          </span>
        )}
        
        {/* 左侧图标 */}
        {leftIcon && !isLoading && (
          <span className="mr-2">{leftIcon}</span>
        )}
        
        {/* 按钮内容 */}
        <span className={clsx(isLoading && 'invisible')}>{children}</span>
        
        {/* 右侧图标 */}
        {rightIcon && !isLoading && (
          <span className="ml-2">{rightIcon}</span>
        )}
      </button>
    )
  }
)

Button.displayName = 'Button'

export default Button
