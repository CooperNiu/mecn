// ErrorBoundary 组件
// 用于捕获和处理 React 组件错误

import { Component, ErrorInfo, ReactNode } from 'react'
import { AlertTriangle, RefreshCw, Home } from 'lucide-react'
import { Button } from './Button'

// ErrorBoundary 属性
interface ErrorBoundaryProps {
  children: ReactNode
  fallback?: ReactNode
  onError?: (error: Error, errorInfo: ErrorInfo) => void
}

// ErrorBoundary 状态
interface ErrorBoundaryState {
  hasError: boolean
  error: Error | null
  errorInfo: ErrorInfo | null
}

// ErrorBoundary 组件
export class ErrorBoundary extends Component<ErrorBoundaryProps, ErrorBoundaryState> {
  constructor(props: ErrorBoundaryProps) {
    super(props)
    this.state = {
      hasError: false,
      error: null,
      errorInfo: null,
    }
  }

  static getDerivedStateFromError(error: Error): Partial<ErrorBoundaryState> {
    return {
      hasError: true,
      error,
    }
  }

  componentDidCatch(error: Error, errorInfo: ErrorInfo): void {
    this.setState({
      errorInfo,
    })

    // 调用 onError 回调
    this.props.onError?.(error, errorInfo)

    // 输出错误信息到控制台
    console.error('ErrorBoundary caught an error:', error, errorInfo)
  }

  handleRetry = (): void => {
    this.setState({
      hasError: false,
      error: null,
      errorInfo: null,
    })
  }

  handleGoHome = (): void => {
    window.location.href = '/'
  }

  render(): ReactNode {
    if (this.state.hasError) {
      // 如果提供了自定义 fallback，使用它
      if (this.props.fallback) {
        return this.props.fallback
      }

      // 默认错误 UI
      return (
        <div className="min-h-[400px] flex items-center justify-center p-6">
          <div className="max-w-md w-full text-center">
            {/* 错误图标 */}
            <div className="w-16 h-16 mx-auto mb-6 bg-red-100 dark:bg-red-900/30 rounded-full flex items-center justify-center">
              <AlertTriangle className="w-8 h-8 text-red-600 dark:text-red-400" />
            </div>

            {/* 错误信息 */}
            <h2 className="text-xl font-semibold text-slate-900 dark:text-white mb-2">
              出错了
            </h2>
            <p className="text-slate-600 dark:text-slate-400 mb-6">
              {this.state.error?.message || '发生了未知错误，请稍后重试'}
            </p>

            {/* 错误详情 */}
            {this.state.errorInfo && (
              <div className="mb-6 p-4 bg-slate-100 dark:bg-slate-800 rounded-lg text-left">
                <p className="text-sm font-medium text-slate-700 dark:text-slate-300 mb-2">
                  错误详情：
                </p>
                <pre className="text-xs text-slate-600 dark:text-slate-400 overflow-auto max-h-32">
                  {this.state.error?.stack}
                </pre>
              </div>
            )}

            {/* 操作按钮 */}
            <div className="flex items-center justify-center gap-3">
              <Button
                variant="secondary"
                leftIcon={<RefreshCw className="w-4 h-4" />}
                onClick={this.handleRetry}
              >
                重试
              </Button>
              <Button
                variant="primary"
                leftIcon={<Home className="w-4 h-4" />}
                onClick={this.handleGoHome}
              >
                返回首页
              </Button>
            </div>
          </div>
        </div>
      )
    }

    return this.props.children
  }
}

// 简化版 ErrorBoundary Hook（用于函数组件）
export function useErrorBoundary() {
  const [error, setError] = React.useState<Error | null>(null)

  const resetError = () => setError(null)

  const captureError = (error: Error) => {
    setError(error)
    console.error('Error captured:', error)
  }

  if (error) {
    throw error
  }

  return { captureError, resetError }
}

// 导入 React
import React from 'react'

export default ErrorBoundary
