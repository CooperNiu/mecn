// 数据源管理页面
// 使用新组件重构，展示数据源管理功能

import { Database, CheckCircle, AlertCircle, Clock, RefreshCw, Settings, Download, Plus, BarChart3 } from 'lucide-react'
import { Card, CardHeader, CardContent, Button, Table, Column } from './base'

// 数据源数据
const dataSources = [
  {
    id: 'fred',
    name: 'FRED',
    description: '圣路易斯联邦储备经济数据库',
    status: 'connected' as const,
    indicators: 50,
    lastUpdate: '2 分钟前',
    category: '国际',
  },
  {
    id: 'worldbank',
    name: 'World Bank',
    description: '世界银行公开数据库',
    status: 'connected' as const,
    indicators: 120,
    lastUpdate: '5 分钟前',
    category: '国际',
  },
  {
    id: 'akshare',
    name: 'AkShare',
    description: '中国市场数据接口',
    status: 'connected' as const,
    indicators: 30,
    lastUpdate: '1 分钟前',
    category: '国内',
  },
  {
    id: 'imf',
    name: 'IMF',
    description: '国际货币基金组织',
    status: 'disconnected' as const,
    indicators: 0,
    lastUpdate: '未连接',
    category: '国际',
  },
  {
    id: 'wind',
    name: 'Wind',
    description: '万得金融数据库',
    status: 'connected' as const,
    indicators: 200,
    lastUpdate: '10 分钟前',
    category: '国内',
  },
]

// 表格列定义
const columns: Column<typeof dataSources[0]>[] = [
  { 
    key: 'name', 
    header: '数据源名称',
    render: (item) => (
      <div className="flex items-center gap-3">
        <div className="w-10 h-10 bg-primary-100 dark:bg-primary-900/30 rounded-lg flex items-center justify-center">
          <Database className="w-5 h-5 text-primary-600 dark:text-primary-400" />
        </div>
        <div>
          <p className="font-medium text-slate-900 dark:text-white">{item.name}</p>
          <p className="text-xs text-slate-500 dark:text-slate-400">{item.description}</p>
        </div>
      </div>
    )
  },
  { 
    key: 'category', 
    header: '分类',
    align: 'center'
  },
  { 
    key: 'indicators', 
    header: '指标数量',
    align: 'center',
    render: (item) => (
      <span className="font-semibold text-slate-900 dark:text-white">{item.indicators}</span>
    )
  },
  { 
    key: 'status', 
    header: '状态',
    align: 'center',
    render: (item) => (
      <div className="flex items-center justify-center gap-2">
        {item.status === 'connected' ? (
          <>
            <CheckCircle className="w-4 h-4 text-green-500" />
            <span className="text-green-600 dark:text-green-400 font-medium">已连接</span>
          </>
        ) : (
          <>
            <AlertCircle className="w-4 h-4 text-red-500" />
            <span className="text-red-600 dark:text-red-400 font-medium">未连接</span>
          </>
        )}
      </div>
    )
  },
  { 
    key: 'lastUpdate', 
    header: '最后更新',
    render: (item) => (
      <div className="flex items-center gap-1 text-slate-500 dark:text-slate-400">
        <Clock className="w-4 h-4" />
        <span>{item.lastUpdate}</span>
      </div>
    )
  },
  { 
    key: 'id', 
    header: '操作',
    align: 'right',
    render: () => (
      <Button variant="ghost" size="sm">
        <Settings className="w-4 h-4" />
      </Button>
    )
  },
]

export default function DataSources() {
  // 统计数据
  const stats = {
    total: dataSources.length,
    connected: dataSources.filter(s => s.status === 'connected').length,
    totalIndicators: dataSources.reduce((sum, s) => sum + s.indicators, 0),
  }

  return (
    <div className="p-6 space-y-6">
      {/* 页面标题 */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-slate-900 dark:text-white">数据源管理</h1>
          <p className="mt-1 text-sm text-slate-500 dark:text-slate-400">
            管理和监控数据源连接状态
          </p>
        </div>
        <div className="flex items-center gap-3">
          <Button variant="secondary" leftIcon={<RefreshCw className="w-4 h-4" />}>
            刷新状态
          </Button>
          <Button variant="secondary" leftIcon={<Download className="w-4 h-4" />}>
            导出配置
          </Button>
          <Button variant="primary" leftIcon={<Plus className="w-4 h-4" />}>
            添加数据源
          </Button>
        </div>
      </div>

      {/* 统计卡片 */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        <Card className="p-6">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm font-medium text-slate-600 dark:text-slate-400">数据源总数</p>
              <p className="text-3xl font-bold text-slate-900 dark:text-white mt-1">{stats.total}</p>
            </div>
            <div className="p-3 bg-primary-100 dark:bg-primary-900/30 rounded-xl">
              <Database className="w-6 h-6 text-primary-600 dark:text-primary-400" />
            </div>
          </div>
        </Card>
        
        <Card className="p-6">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm font-medium text-slate-600 dark:text-slate-400">已连接</p>
              <p className="text-3xl font-bold text-green-600 dark:text-green-400 mt-1">{stats.connected}</p>
            </div>
            <div className="p-3 bg-green-100 dark:bg-green-900/30 rounded-xl">
              <CheckCircle className="w-6 h-6 text-green-600 dark:text-green-400" />
            </div>
          </div>
        </Card>
        
        <Card className="p-6">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm font-medium text-slate-600 dark:text-slate-400">指标总数</p>
              <p className="text-3xl font-bold text-slate-900 dark:text-white mt-1">{stats.totalIndicators}</p>
            </div>
            <div className="p-3 bg-blue-100 dark:bg-blue-900/30 rounded-xl">
              <BarChart3 className="w-6 h-6 text-blue-600 dark:text-blue-400" />
            </div>
          </div>
        </Card>
      </div>

      {/* 数据源列表 */}
      <Card>
        <CardHeader 
          title="数据源列表" 
          subtitle="所有配置的数据源"
          action={
            <Button variant="ghost" size="sm">
              查看全部
            </Button>
          }
        />
        <CardContent>
          <Table 
            columns={columns} 
            data={dataSources}
            rowKey="id"
            sortable
            hoverable
          />
        </CardContent>
      </Card>
    </div>
  )
}
