// Modal 组件
// 支持多种尺寸、标题、内容、底部操作

import { ReactNode } from 'react'
import { clsx } from 'clsx'

// Modal 属性
export interface ModalProps {
  isOpen: boolean
  onClose: () => void
  size?: 'sm' | 'md' | 'lg' | 'xl' | 'full'
  title?: string
  subtitle?: string
  children: ReactNode
  footer?: ReactNode
  closeOnOverlayClick?: boolean
  closeOnEsc?: boolean
  showCloseButton?: boolean
}

// Modal 头部属性
export interface ModalHeaderProps {
  title?: string
  subtitle?: string
  onClose?: () => void
  showCloseButton?: boolean
}

// Modal 内容属性
export interface ModalContentProps {
  children: ReactNode
  className?: string
}

// Modal 底部属性
export interface ModalFooterProps {
  children: ReactNode
  align?: 'left' | 'center' | 'right' | 'between'
}

// 尺寸样式映射
const sizeStyles: Record<string, string> = {
  sm: 'max-w-md',
  md: 'max-w-lg',
  lg: 'max-w-2xl',
  xl: 'max-w-4xl',
  full: 'max-w-6xl',
}

// Modal 主体
export function Modal({
  isOpen,
  onClose,
  size = 'md',
  title,
  subtitle,
  children,
  footer,
  closeOnOverlayClick = true,
  closeOnEsc = true,
  showCloseButton = true,
}: ModalProps) {
  // 处理 ESC 键
  const handleKeyDown = (e: React.KeyboardEvent) => {
    if (e.key === 'Escape' && closeOnEsc) {
      onClose()
    }
  }
  
  // 处理遮罩点击
  const handleOverlayClick = () => {
    if (closeOnOverlayClick) {
      onClose()
    }
  }
  
  if (!isOpen) return null
  
  return (
    <div
      className="fixed inset-0 z-50 overflow-y-auto"
      aria-labelledby="modal-title"
      role="dialog"
      aria-modal="true"
      onKeyDown={handleKeyDown}
    >
      {/* 遮罩层 */}
      <div
        className="fixed inset-0 bg-black/50 backdrop-blur-sm transition-opacity"
        onClick={handleOverlayClick}
      />
      
      {/* 居中容器 */}
      <div className="flex min-h-full items-end justify-center p-4 text-center sm:items-center sm:p-0">
        {/* 模态框内容 */}
        <div
          className={clsx(
            'relative transform overflow-hidden rounded-2xl bg-white dark:bg-slate-800',
            'text-left align-bottom shadow-xl transition-all',
            'sm:my-8 sm:w-full sm:align-middle',
            sizeStyles[size]
          )}
          onClick={(e) => e.stopPropagation()}
        >
          {/* 头部 */}
          {(title || showCloseButton) && (
            <div className="px-6 py-4 border-b border-slate-200 dark:border-slate-700">
              <div className="flex items-center justify-between">
                <div>
                  {title && (
                    <h3
                      className="text-lg font-semibold text-slate-900 dark:text-white"
                      id="modal-title"
                    >
                      {title}
                    </h3>
                  )}
                  {subtitle && (
                    <p className="mt-1 text-sm text-slate-500 dark:text-slate-400">
                      {subtitle}
                    </p>
                  )}
                </div>
                {showCloseButton && (
                  <button
                    type="button"
                    className="rounded-lg p-2 text-slate-400 hover:text-slate-500 hover:bg-slate-100 dark:hover:bg-slate-700 transition-colors"
                    onClick={onClose}
                  >
                    <span className="sr-only">关闭</span>
                    <svg
                      className="h-5 w-5"
                      fill="none"
                      viewBox="0 0 24 24"
                      strokeWidth="1.5"
                      stroke="currentColor"
                    >
                      <path
                        strokeLinecap="round"
                        strokeLinejoin="round"
                        d="M6 18L18 6M6 6l12 12"
                      />
                    </svg>
                  </button>
                )}
              </div>
            </div>
          )}
          
          {/* 内容 */}
          <div className="px-6 py-4">{children}</div>
          
          {/* 底部 */}
          {footer && (
            <div className="px-6 py-4 border-t border-slate-200 dark:border-slate-700 bg-slate-50 dark:bg-slate-900">
              {footer}
            </div>
          )}
        </div>
      </div>
    </div>
  )
}

// Modal 头部组件
export const ModalHeader = ({
  title,
  subtitle,
  onClose,
  showCloseButton = true,
}: ModalHeaderProps) => {
  return (
    <div className="px-6 py-4 border-b border-slate-200 dark:border-slate-700">
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
        {showCloseButton && onClose && (
          <button
            type="button"
            className="rounded-lg p-2 text-slate-400 hover:text-slate-500 hover:bg-slate-100 dark:hover:bg-slate-700 transition-colors"
            onClick={onClose}
          >
            <span className="sr-only">关闭</span>
            <svg
              className="h-5 w-5"
              fill="none"
              viewBox="0 0 24 24"
              strokeWidth="1.5"
              stroke="currentColor"
            >
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                d="M6 18L18 6M6 6l12 12"
              />
            </svg>
          </button>
        )}
      </div>
    </div>
  )
}

// Modal 内容组件
export const ModalContent = ({
  children,
  className,
}: ModalContentProps) => {
  return (
    <div className={clsx('px-6 py-4', className)}>{children}</div>
  )
}

// Modal 底部组件
export const ModalFooter = ({
  children,
  align = 'right',
}: ModalFooterProps) => {
  const alignStyles = {
    left: 'justify-start',
    center: 'justify-center',
    right: 'justify-end',
    between: 'justify-between',
  }
  
  return (
    <div
      className={clsx(
        'px-6 py-4 border-t border-slate-200 dark:border-slate-700 bg-slate-50 dark:bg-slate-900',
        'flex items-center gap-3',
        alignStyles[align]
      )}
    >
      {children}
    </div>
  )
}

export default Modal
