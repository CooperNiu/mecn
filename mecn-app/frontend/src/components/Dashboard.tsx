// 仪表盘页面
// 使用新组件重构，展示平台概览

import { Activity, TrendingUp, AlertTriangle, CheckCircle, BarChart3, Network, Waves } from 'lucide-react'
import { Card, CardHeader, CardContent, StatCard, StatCardGrid, Button } from './base'

// 统计数据
const stats = [
  { 
    name: '活跃指标', 
    value: '42', 
    change: '+12%', 
    icon: <Activity className="w-6 h-6" />,
    color: 'primary' as const,
    trend: 'up' as const
  },
  { 
    name: '因果关系', 
    value: '156', 
    change: '+8%', 
    icon: <TrendingUp className="w-6 h-6" />,
    color: 'success' as const,
    trend: 'up' as const
  },
  { 
    name: '风险预警', 
    value: '3', 
    change: '-25%', 
    icon: <AlertTriangle className="w-6 h-6" />,
    color: 'warning' as const,
    trend: 'down' as const
  },
  { 
    name: '网络健康', 
    value: '98%', 
    change: '+2%', 
    icon: <CheckCircle className="w-6 h-6" />,
    color: 'success' as const,
    trend: 'up' as const
  },
]

// 预警数据
const alerts = [
  {
    id: 1,
    type: 'high' as const,
    title: 'GDP 增速放缓预警',
    time: '2024-01-15 10:30',
    description: 'GDP 增速连续3个月下降，触发预警阈值',
  },
  {
    id: 2,
    type: 'medium' as const,
    title: 'CPI 指数异常波动',
    time: '2024-01-15 09:15',
    description: 'CPI 指数波动超过正常范围',
  },
  {
    id: 3,
    type: 'low' as const,
    title: '网络稳定性恢复',
    time: '2024-01-15 08:45',
    description: '网络稳定性指标已恢复正常',
  },
]

// 快捷操作
const quickActions = [
  { name: '构建网络', icon: <Network className="w-8 h-8" />, color: 'text-primary-600 dark:text-primary-400' },
  { name: '涟漪模拟', icon: <Waves className="w-8 h-8" />, color: 'text-green-600 dark:text-green-400' },
  { name: '风险分析', icon: <AlertTriangle className="w-8 h-8" />, color: 'text-yellow-600 dark:text-yellow-400' },
  { name: '数据导入', icon: <BarChart3 className="w-8 h-8" />, color: 'text-blue-600 dark:text-blue-400' },
]

// 预警类型样式
const alertTypeStyles = {
  high: {
    bg: 'bg-yellow-50 dark:bg-yellow-900/20',
    border: 'border-yellow-200 dark:border-yellow-800',
    icon: 'text-yellow-600 dark:text-yellow-400',
    badge: 'bg-yellow-100 dark:bg-yellow-900/50 text-yellow-800 dark:text-yellow-300',
  },
  medium: {
    bg: 'bg-blue-50 dark:bg-blue-900/20',
    border: 'border-blue-200 dark:border-blue-800',
    icon: 'text-blue-600 dark:text-blue-400',
    badge: 'bg-blue-100 dark:bg-blue-900/50 text-blue-800 dark:text-blue-300',
  },
  low: {
    bg: 'bg-green-50 dark:bg-green-900/20',
    border: 'border-green-200 dark:border-green-800',
    icon: 'text-green-600 dark:text-green-400',
    badge: 'bg-green-100 dark:bg-green-900/50 text-green-800 dark:text-green-300',
  },
}

export default function Dashboard() {
  return (
    <div className="p-6 space-y-6">
      {/* 页面标题 */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-slate-900 dark:text-white">仪表盘</h1>
          <p className="mt-1 text-sm text-slate-500 dark:text-slate-400">
            宏观经济因果网络分析平台概览
          </p>
        </div>
        <Button variant="primary" leftIcon={<BarChart3 className="w-4 h-4" />}>
          生成报告
        </Button>
      </div>
      
      {/* 统计卡片网格 */}
      <StatCardGrid columns={4}>
        {stats.map((stat) => (
          <StatCard
            key={stat.name}
            title={stat.name}
            value={stat.value}
            trend={stat.trend}
            change={stat.change}
            icon={stat.icon}
            color={stat.color}
          />
        ))}
      </StatCardGrid>

      {/* 主内容网格 */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* 网络可视化 - 占据 2 列 */}
        <Card className="lg:col-span-2">
          <CardHeader 
            title="因果网络概览" 
            subtitle="实时网络状态监控"
            action={
              <Button variant="ghost" size="sm">
                查看详情
              </Button>
            }
          />
          <CardContent>
            <div className="h-80 bg-slate-50 dark:bg-slate-700/50 rounded-lg flex items-center justify-center border border-dashed border-slate-300 dark:border-slate-600">
              <div className="text-center">
                <Network className="w-16 h-16 text-slate-400 dark:text-slate-500 mx-auto mb-4" />
                <p className="text-lg font-medium text-slate-600 dark:text-slate-400">网络可视化区域</p>
                <p className="text-sm text-slate-500 dark:text-slate-500 mt-2">D3.js 力导向图</p>
                <Button variant="primary" size="sm" className="mt-4">
                  构建网络
                </Button>
              </div>
            </div>
          </CardContent>
        </Card>

        {/* 最近预警 */}
        <Card>
          <CardHeader 
            title="最近预警" 
            subtitle="系统风险监控"
            action={
              <Button variant="ghost" size="sm">
                查看全部
              </Button>
            }
          />
          <CardContent>
            <div className="space-y-4">
              {alerts.map((alert) => {
                const styles = alertTypeStyles[alert.type]
                return (
                  <div 
                    key={alert.id}
                    className={`flex items-start p-4 rounded-lg border ${styles.bg} ${styles.border}`}
                  >
                    <AlertTriangle className={`w-5 h-5 ${styles.icon} mt-0.5`} />
                    <div className="ml-3 flex-1">
                      <div className="flex items-center justify-between">
                        <p className="text-sm font-medium text-slate-900 dark:text-white">{alert.title}</p>
                        <span className={`px-2 py-1 text-xs font-medium rounded-full ${styles.badge}`}>
                          {alert.type === 'high' ? '高' : alert.type === 'medium' ? '中' : '低'}
                        </span>
                      </div>
                      <p className="text-xs text-slate-500 dark:text-slate-400 mt-1">{alert.time}</p>
                      <p className="text-sm text-slate-600 dark:text-slate-300 mt-2">{alert.description}</p>
                    </div>
                  </div>
                )
              })}
            </div>
          </CardContent>
        </Card>
      </div>

      {/* 快捷操作和最近分析 */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* 快捷操作 */}
        <Card>
          <CardHeader title="快捷操作" subtitle="常用功能入口" />
          <CardContent>
            <div className="grid grid-cols-2 gap-4">
              {quickActions.map((action) => (
                <button
                  key={action.name}
                  className="flex flex-col items-center p-6 bg-slate-50 dark:bg-slate-700/50 rounded-xl hover:bg-slate-100 dark:hover:bg-slate-700 transition-all duration-200 hover:shadow-sm"
                >
                  <div className={action.color}>{action.icon}</div>
                  <span className="text-sm font-medium text-slate-700 dark:text-slate-300 mt-3">{action.name}</span>
                </button>
              ))}
            </div>
          </CardContent>
        </Card>

        {/* 最近分析 */}
        <Card>
          <CardHeader 
            title="最近分析" 
            subtitle="历史分析记录"
            action={
              <Button variant="ghost" size="sm">
                查看全部
              </Button>
            }
          />
          <CardContent>
            <div className="space-y-4">
              {[
                { name: '宏观经济网络分析', time: '2 小时前', status: 'completed' },
                { name: 'GDP 因果关系挖掘', time: '5 小时前', status: 'completed' },
                { name: 'CPI 涟漪效应模拟', time: '1 天前', status: 'completed' },
              ].map((item, index) => (
                <div key={index} className="flex items-center justify-between p-4 bg-slate-50 dark:bg-slate-700/50 rounded-lg">
                  <div className="flex items-center gap-3">
                    <div className="w-10 h-10 bg-primary-100 dark:bg-primary-900/30 rounded-lg flex items-center justify-center">
                      <BarChart3 className="w-5 h-5 text-primary-600 dark:text-primary-400" />
                    </div>
                    <div>
                      <p className="text-sm font-medium text-slate-900 dark:text-white">{item.name}</p>
                      <p className="text-xs text-slate-500 dark:text-slate-400">{item.time}</p>
                    </div>
                  </div>
                  <Button variant="ghost" size="sm">
                    查看
                  </Button>
                </div>
              ))}
            </div>
          </CardContent>
        </Card>
      </div>
    </div>
  )
}
