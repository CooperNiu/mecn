// Table 组件
// 支持斑马纹、排序、分页、选择

import { useState, useMemo, ReactNode } from 'react'
import { clsx } from 'clsx'
import { ChevronUp, ChevronDown, ChevronsUpDown, ChevronLeft, ChevronRight } from 'lucide-react'

// 列定义
export interface Column<T> {
  key: string
  header: string
  width?: string
  sortable?: boolean
  render?: (item: T, index: number) => ReactNode
  align?: 'left' | 'center' | 'right'
}

// 排序状态
export interface SortState {
  key: string
  direction: 'asc' | 'desc'
}

// 分页状态
export interface PaginationState {
  page: number
  pageSize: number
  total: number
}

// Table 属性
export interface TableProps<T> {
  columns: Column<T>[]
  data: T[]
  sortable?: boolean
  striped?: boolean
  hoverable?: boolean
  selectable?: boolean
  selectedRows?: string[]
  onSelectionChange?: (selectedRows: string[]) => void
  pagination?: PaginationState
  onPageChange?: (page: number) => void
  onSort?: (sort: SortState) => void
  emptyMessage?: string
  loading?: boolean
  rowKey?: keyof T | ((item: T) => string)
  onRowClick?: (item: T) => void
}

// 排序图标组件
function SortIcon({ direction }: { direction?: 'asc' | 'desc' }) {
  if (!direction) {
    return <ChevronsUpDown className="w-4 h-4 text-slate-400" />
  }
  return direction === 'asc' 
    ? <ChevronUp className="w-4 h-4 text-primary-600" />
    : <ChevronDown className="w-4 h-4 text-primary-600" />
}

// Table 组件
export function Table<T extends Record<string, unknown>>({
  columns,
  data,
  sortable = false,
  striped = true,
  hoverable = true,
  selectable = false,
  selectedRows = [],
  onSelectionChange,
  pagination,
  onPageChange,
  onSort,
  emptyMessage = '暂无数据',
  loading = false,
  rowKey = 'id' as keyof T,
  onRowClick,
}: TableProps<T>) {
  const [sort, setSort] = useState<SortState | null>(null)

  // 获取行唯一标识
  const getRowKey = (item: T): string => {
    if (typeof rowKey === 'function') {
      return rowKey(item)
    }
    return String(item[rowKey])
  }

  // 排序数据
  const sortedData = useMemo(() => {
    if (!sort || !sortable) return data

    return [...data].sort((a, b) => {
      const aVal = a[sort.key]
      const bVal = b[sort.key]

      if (aVal === bVal) return 0
      if (aVal === null || aVal === undefined) return 1
      if (bVal === null || bVal === undefined) return -1

      const comparison = String(aVal).localeCompare(String(bVal))
      return sort.direction === 'asc' ? comparison : -comparison
    })
  }, [data, sort, sortable])

  // 处理排序
  const handleSort = (key: string) => {
    if (!sortable) return

    const newSort: SortState = {
      key,
      direction: sort?.key === key && sort.direction === 'asc' ? 'desc' : 'asc',
    }
    setSort(newSort)
    onSort?.(newSort)
  }

  // 处理全选
  const handleSelectAll = () => {
    if (!onSelectionChange) return

    if (selectedRows.length === data.length) {
      onSelectionChange([])
    } else {
      onSelectionChange(data.map(getRowKey))
    }
  }

  // 处理行选择
  const handleSelectRow = (key: string) => {
    if (!onSelectionChange) return

    if (selectedRows.includes(key)) {
      onSelectionChange(selectedRows.filter((r) => r !== key))
    } else {
      onSelectionChange([...selectedRows, key])
    }
  }

  // 渲染空状态
  if (data.length === 0 && !loading) {
    return (
      <div className="bg-white dark:bg-slate-800 rounded-xl border border-slate-200 dark:border-slate-700">
        <div className="p-12 text-center">
          <div className="w-16 h-16 mx-auto mb-4 bg-slate-100 dark:bg-slate-700 rounded-full flex items-center justify-center">
            <svg className="w-8 h-8 text-slate-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1.5} d="M20 13V6a2 2 0 00-2-2H6a2 2 0 00-2 2v7m16 0v5a2 2 0 01-2 2H6a2 2 0 01-2-2v-5m16 0h-2.586a1 1 0 00-.707.293l-2.414 2.414a1 1 0 01-.707.293h-3.172a1 1 0 01-.707-.293l-2.414-2.414A1 1 0 006.586 13H4" />
            </svg>
          </div>
          <p className="text-slate-500 dark:text-slate-400">{emptyMessage}</p>
        </div>
      </div>
    )
  }

  return (
    <div className="bg-white dark:bg-slate-800 rounded-xl border border-slate-200 dark:border-slate-700 overflow-hidden">
      {/* 加载状态 */}
      {loading && (
        <div className="absolute inset-0 bg-white/50 dark:bg-slate-800/50 flex items-center justify-center z-10">
          <div className="w-8 h-8 border-4 border-primary-600 border-t-transparent rounded-full animate-spin" />
        </div>
      )}

      {/* 表格容器 */}
      <div className="overflow-x-auto relative">
        <table className="min-w-full divide-y divide-slate-200 dark:divide-slate-700">
          {/* 表头 */}
          <thead className="bg-slate-50 dark:bg-slate-700/50">
            <tr>
              {/* 选择列 */}
              {selectable && (
                <th className="w-12 px-4 py-3">
                  <input
                    type="checkbox"
                    checked={selectedRows.length === data.length && data.length > 0}
                    onChange={handleSelectAll}
                    className="w-4 h-4 text-primary-600 bg-white dark:bg-slate-700 border-slate-300 dark:border-slate-600 rounded focus:ring-primary-500"
                  />
                </th>
              )}

              {/* 数据列 */}
              {columns.map((column) => (
                <th
                  key={column.key}
                  className={clsx(
                    'px-4 py-3 text-left text-xs font-medium text-slate-500 dark:text-slate-400 uppercase tracking-wider',
                    column.align === 'center' && 'text-center',
                    column.align === 'right' && 'text-right',
                    sortable && column.sortable !== false && 'cursor-pointer select-none hover:bg-slate-100 dark:hover:bg-slate-700'
                  )}
                  style={{ width: column.width }}
                  onClick={() => column.sortable !== false && handleSort(column.key)}
                >
                  <div className="flex items-center gap-1">
                    <span>{column.header}</span>
                    {sortable && column.sortable !== false && (
                      <SortIcon direction={sort?.key === column.key ? sort.direction : undefined} />
                    )}
                  </div>
                </th>
              ))}
            </tr>
          </thead>

          {/* 表体 */}
          <tbody className="divide-y divide-slate-200 dark:divide-slate-700">
            {sortedData.map((item, index) => {
              const rowKeyValue = getRowKey(item)
              const isSelected = selectedRows.includes(rowKeyValue)

              return (
                <tr
                  key={rowKeyValue}
                  className={clsx(
                    'transition-colors duration-150',
                    striped && index % 2 === 1 && 'bg-slate-50/50 dark:bg-slate-700/30',
                    hoverable && 'hover:bg-slate-50 dark:hover:bg-slate-700/50',
                    isSelected && 'bg-primary-50 dark:bg-primary-900/20',
                    onRowClick && 'cursor-pointer'
                  )}
                  onClick={() => onRowClick?.(item)}
                >
                  {/* 选择列 */}
                  {selectable && (
                    <td className="w-12 px-4 py-3">
                      <input
                        type="checkbox"
                        checked={isSelected}
                        onChange={() => handleSelectRow(rowKeyValue)}
                        onClick={(e) => e.stopPropagation()}
                        className="w-4 h-4 text-primary-600 bg-white dark:bg-slate-700 border-slate-300 dark:border-slate-600 rounded focus:ring-primary-500"
                      />
                    </td>
                  )}

                  {/* 数据列 */}
                  {columns.map((column) => (
                    <td
                      key={column.key}
                      className={clsx(
                        'px-4 py-3 text-sm text-slate-900 dark:text-white whitespace-nowrap',
                        column.align === 'center' && 'text-center',
                        column.align === 'right' && 'text-right'
                      )}
                    >
                      {column.render
                        ? column.render(item, index)
                        : String(item[column.key] ?? '-')}
                    </td>
                  ))}
                </tr>
              )
            })}
          </tbody>
        </table>
      </div>

      {/* 分页 */}
      {pagination && (
        <div className="px-4 py-3 border-t border-slate-200 dark:border-slate-700 bg-slate-50 dark:bg-slate-700/30">
          <div className="flex items-center justify-between">
            <p className="text-sm text-slate-500 dark:text-slate-400">
              共 {pagination.total} 条，第 {pagination.page} / {Math.ceil(pagination.total / pagination.pageSize)} 页
            </p>
            <div className="flex items-center gap-2">
              <button
                onClick={() => onPageChange?.(pagination.page - 1)}
                disabled={pagination.page <= 1}
                className="p-2 text-slate-500 hover:text-slate-700 dark:text-slate-400 dark:hover:text-white disabled:opacity-50 disabled:cursor-not-allowed rounded-lg hover:bg-slate-100 dark:hover:bg-slate-700 transition-colors"
              >
                <ChevronLeft className="w-4 h-4" />
              </button>
              <button
                onClick={() => onPageChange?.(pagination.page + 1)}
                disabled={pagination.page >= Math.ceil(pagination.total / pagination.pageSize)}
                className="p-2 text-slate-500 hover:text-slate-700 dark:text-slate-400 dark:hover:text-white disabled:opacity-50 disabled:cursor-not-allowed rounded-lg hover:bg-slate-100 dark:hover:bg-slate-700 transition-colors"
              >
                <ChevronRight className="w-4 h-4" />
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}

export default Table
