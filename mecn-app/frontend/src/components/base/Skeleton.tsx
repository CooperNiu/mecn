// Skeleton 组件
// 用于加载状态的骨架屏

import { clsx } from 'clsx'

// Skeleton 属性
export interface SkeletonProps {
  className?: string
  variant?: 'text' | 'circular' | 'rectangular' | 'rounded'
  width?: string | number
  height?: string | number
  animation?: 'pulse' | 'wave' | 'none'
  style?: React.CSSProperties
}

// 基础 Skeleton 组件
export function Skeleton({
  className,
  variant = 'text',
  width,
  height,
  animation = 'pulse',
  style,
}: SkeletonProps) {
  const variantStyles = {
    text: 'rounded',
    circular: 'rounded-full',
    rectangular: '',
    rounded: 'rounded-lg',
  }

  const animationStyles = {
    pulse: 'animate-pulse',
    wave: 'animate-wave',
    none: '',
  }

  return (
    <div
      className={clsx(
        'bg-slate-200 dark:bg-slate-700',
        variantStyles[variant],
        animationStyles[animation],
        className
      )}
      style={{
        width,
        height: height || (variant === 'text' ? '1em' : undefined),
        ...style,
      }}
    />
  )
}

// 文本骨架屏
export interface SkeletonTextProps {
  lines?: number
  className?: string
  lineHeight?: string
}

export function SkeletonText({
  lines = 3,
  className,
  lineHeight = '1.5',
}: SkeletonTextProps) {
  return (
    <div className={clsx('space-y-2', className)} style={{ lineHeight }}>
      {Array.from({ length: lines }).map((_, i) => (
        <Skeleton
          key={i}
          variant="text"
          className={clsx(
            'h-4',
            i === lines - 1 && 'w-3/4' // 最后一行稍短
          )}
        />
      ))}
    </div>
  )
}

// 头像骨架屏
export interface SkeletonAvatarProps {
  size?: 'sm' | 'md' | 'lg' | 'xl'
  className?: string
}

export function SkeletonAvatar({
  size = 'md',
  className,
}: SkeletonAvatarProps) {
  const sizeStyles = {
    sm: 'w-8 h-8',
    md: 'w-10 h-10',
    lg: 'w-12 h-12',
    xl: 'w-16 h-16',
  }

  return (
    <Skeleton
      variant="circular"
      className={clsx(sizeStyles[size], className)}
    />
  )
}

// 标题骨架屏
export interface SkeletonHeadingProps {
  level?: 1 | 2 | 3 | 4 | 5 | 6
  className?: string
}

export function SkeletonHeading({
  level = 2,
  className,
}: SkeletonHeadingProps) {
  const heightStyles = {
    1: 'h-8',
    2: 'h-7',
    3: 'h-6',
    4: 'h-5',
    5: 'h-4',
    6: 'h-4',
  }

  return (
    <Skeleton
      variant="text"
      className={clsx(heightStyles[level], 'w-1/2', className)}
    />
  )
}

// 卡片骨架屏
export interface SkeletonCardProps {
  className?: string
  showAvatar?: boolean
  showImage?: boolean
  imageHeight?: string
}

export function SkeletonCard({
  className,
  showAvatar = false,
  showImage = false,
  imageHeight = '200px',
}: SkeletonCardProps) {
  return (
    <div
      className={clsx(
        'bg-white dark:bg-slate-800 rounded-xl border border-slate-200 dark:border-slate-700 p-6',
        className
      )}
    >
      {showImage && (
        <Skeleton
          variant="rectangular"
          className="w-full mb-4 -mx-6 -mt-6 rounded-t-xl"
          style={{ height: imageHeight }}
        />
      )}
      
      <div className="flex items-center gap-4 mb-4">
        {showAvatar && <SkeletonAvatar />}
        <div className="flex-1">
          <SkeletonHeading level={3} className="mb-2" />
          <SkeletonText lines={1} className="w-1/3" />
        </div>
      </div>
      
      <SkeletonText lines={3} />
    </div>
  )
}

// 表格骨架屏
export interface SkeletonTableProps {
  rows?: number
  columns?: number
  className?: string
}

export function SkeletonTable({
  rows = 5,
  columns = 4,
  className,
}: SkeletonTableProps) {
  return (
    <div
      className={clsx(
        'bg-white dark:bg-slate-800 rounded-xl border border-slate-200 dark:border-slate-700 overflow-hidden',
        className
      )}
    >
      {/* 表头 */}
      <div className="flex items-center gap-4 p-4 bg-slate-50 dark:bg-slate-700/50 border-b border-slate-200 dark:border-slate-700">
        {Array.from({ length: columns }).map((_, i) => (
          <Skeleton key={i} variant="text" className="h-4 flex-1" />
        ))}
      </div>
      
      {/* 表体 */}
      <div className="divide-y divide-slate-200 dark:divide-slate-700">
        {Array.from({ length: rows }).map((_, rowIndex) => (
          <div key={rowIndex} className="flex items-center gap-4 p-4">
            {Array.from({ length: columns }).map((_, colIndex) => (
              <Skeleton key={colIndex} variant="text" className="h-4 flex-1" />
            ))}
          </div>
        ))}
      </div>
    </div>
  )
}

// 列表骨架屏
export interface SkeletonListProps {
  items?: number
  className?: string
  showAvatar?: boolean
}

export function SkeletonList({
  items = 5,
  className,
  showAvatar = false,
}: SkeletonListProps) {
  return (
    <div className={clsx('space-y-4', className)}>
      {Array.from({ length: items }).map((_, i) => (
        <div key={i} className="flex items-center gap-4">
          {showAvatar && <SkeletonAvatar />}
          <div className="flex-1">
            <Skeleton variant="text" className="h-4 w-1/3 mb-2" />
            <Skeleton variant="text" className="h-3 w-2/3" />
          </div>
        </div>
      ))}
    </div>
  )
}

// 统计卡片骨架屏
export interface SkeletonStatCardProps {
  className?: string
}

export function SkeletonStatCard({ className }: SkeletonStatCardProps) {
  return (
    <div
      className={clsx(
        'bg-white dark:bg-slate-800 rounded-xl border border-slate-200 dark:border-slate-700 p-6',
        className
      )}
    >
      <div className="flex items-center justify-between">
        <div className="flex-1">
          <Skeleton variant="text" className="h-4 w-20 mb-3" />
          <Skeleton variant="text" className="h-8 w-24" />
        </div>
        <Skeleton variant="rounded" className="w-12 h-12" />
      </div>
      <Skeleton variant="text" className="h-4 w-16 mt-4" />
    </div>
  )
}

// 图表骨架屏
export interface SkeletonChartProps {
  className?: string
  height?: string
}

export function SkeletonChart({
  className,
  height = '300px',
}: SkeletonChartProps) {
  return (
    <div
      className={clsx(
        'bg-white dark:bg-slate-800 rounded-xl border border-slate-200 dark:border-slate-700 p-6',
        className
      )}
    >
      <SkeletonHeading level={3} className="mb-4" />
      <Skeleton
        variant="rectangular"
        className="w-full"
        style={{ height }}
      />
    </div>
  )
}

// 页面骨架屏
export interface SkeletonPageProps {
  className?: string
}

export function SkeletonPage({ className }: SkeletonPageProps) {
  return (
    <div className={clsx('p-6 space-y-6', className)}>
      {/* 页面标题 */}
      <div className="flex items-center justify-between">
        <div>
          <SkeletonHeading level={1} className="mb-2" />
          <SkeletonText lines={1} className="w-64" />
        </div>
        <Skeleton variant="rounded" className="w-32 h-10" />
      </div>
      
      {/* 统计卡片 */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        {Array.from({ length: 4 }).map((_, i) => (
          <SkeletonStatCard key={i} />
        ))}
      </div>
      
      {/* 内容区域 */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <div className="lg:col-span-2">
          <SkeletonCard showImage imageHeight="300px" />
        </div>
        <div>
          <SkeletonCard />
        </div>
      </div>
    </div>
  )
}

export default Skeleton
