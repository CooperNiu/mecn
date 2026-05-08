// Toast 组件
// 支持多种类型、自动消失、手动关闭

import { useState, useEffect, useCallback, ReactNode, createContext, useContext } from 'react'
import { clsx } from 'clsx'
import { X, CheckCircle, AlertTriangle, AlertCircle, Info } from 'lucide-react'

// Toast 类型
export type ToastType = 'success' | 'warning' | 'error' | 'info'

// Toast 属性
export interface ToastProps {
  id: string
  type: ToastType
  title: string
  message?: string
  duration?: number
  onClose: (id: string) => void
}

// Toast 上下文类型
interface ToastContextType {
  toasts: Omit<ToastProps, 'onClose'>[]
  addToast: (toast: Omit<ToastProps, 'id' | 'onClose'>) => void
  removeToast: (id: string) => void
}

// Toast 上下文
const ToastContext = createContext<ToastContextType | undefined>(undefined)

// 图标映射
const iconMap: Record<ToastType, ReactNode> = {
  success: <CheckCircle className="w-5 h-5 text-green-500" />,
  warning: <AlertTriangle className="w-5 h-5 text-yellow-500" />,
  error: <AlertCircle className="w-5 h-5 text-red-500" />,
  info: <Info className="w-5 h-5 text-blue-500" />,
}

// 样式映射
const stylesMap: Record<ToastType, string> = {
  success: 'bg-green-50 dark:bg-green-900/30 border-green-200 dark:border-green-800',
  warning: 'bg-yellow-50 dark:bg-yellow-900/30 border-yellow-200 dark:border-yellow-800',
  error: 'bg-red-50 dark:bg-red-900/30 border-red-200 dark:border-red-800',
  info: 'bg-blue-50 dark:bg-blue-900/30 border-blue-200 dark:border-blue-800',
}

// Toast 组件
export function Toast({
  id,
  type,
  title,
  message,
  duration = 5000,
  onClose,
}: ToastProps) {
  const [isVisible, setIsVisible] = useState(true)

  // 自动消失
  useEffect(() => {
    if (duration <= 0) return

    const timer = setTimeout(() => {
      setIsVisible(false)
      setTimeout(() => onClose(id), 300) // 等待动画完成
    }, duration)

    return () => clearTimeout(timer)
  }, [duration, id, onClose])

  // 手动关闭
  const handleClose = () => {
    setIsVisible(false)
    setTimeout(() => onClose(id), 300)
  }

  return (
    <div
      className={clsx(
        'flex items-start gap-3 p-4 rounded-lg border shadow-lg',
        'transition-all duration-300 transform',
        stylesMap[type],
        isVisible ? 'translate-x-0 opacity-100' : 'translate-x-full opacity-0'
      )}
    >
      {/* 图标 */}
      <div className="flex-shrink-0">{iconMap[type]}</div>

      {/* 内容 */}
      <div className="flex-1 min-w-0">
        <p className="text-sm font-medium text-slate-900 dark:text-white">{title}</p>
        {message && (
          <p className="mt-1 text-sm text-slate-600 dark:text-slate-300">{message}</p>
        )}
      </div>

      {/* 关闭按钮 */}
      <button
        onClick={handleClose}
        className="flex-shrink-0 p-1 text-slate-400 hover:text-slate-600 dark:hover:text-slate-200 rounded-lg hover:bg-slate-100 dark:hover:bg-slate-700 transition-colors"
      >
        <X className="w-4 h-4" />
      </button>
    </div>
  )
}

// Toast 容器
export function ToastContainer() {
  const { toasts, removeToast } = useContext(ToastContext)!

  return (
    <div className="fixed top-4 right-4 z-50 flex flex-col gap-2 w-80">
      {toasts.map((toast) => (
        <Toast key={toast.id} {...toast} onClose={removeToast} />
      ))}
    </div>
  )
}

// Toast 提供者
export function ToastProvider({ children }: { children: ReactNode }) {
  const [toasts, setToasts] = useState<Omit<ToastProps, 'onClose'>[]>([])

  const addToast = useCallback((toast: Omit<ToastProps, 'id' | 'onClose'>) => {
    const id = Math.random().toString(36).substring(2, 9)
    setToasts((prev) => [...prev, { ...toast, id }])
  }, [])

  const removeToast = useCallback((id: string) => {
    setToasts((prev) => prev.filter((t) => t.id !== id))
  }, [])

  return (
    <ToastContext.Provider value={{ toasts, addToast, removeToast }}>
      {children}
      <ToastContainer />
    </ToastContext.Provider>
  )
}

// Toast Hook
export function useToast() {
  const context = useContext(ToastContext)
  if (!context) {
    throw new Error('useToast must be used within a ToastProvider')
  }
  return context
}

// 便捷方法
export function useToastActions() {
  const { addToast } = useToast()

  return {
    success: (title: string, message?: string) => addToast({ type: 'success', title, message }),
    warning: (title: string, message?: string) => addToast({ type: 'warning', title, message }),
    error: (title: string, message?: string) => addToast({ type: 'error', title, message }),
    info: (title: string, message?: string) => addToast({ type: 'info', title, message }),
  }
}

export default Toast
