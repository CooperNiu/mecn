// 网络分析页面
// 使用新组件重构，展示因果网络分析功能

import { useState } from 'react'
import { Network, GitBranch, Settings, Download, RefreshCw } from 'lucide-react'
import { Card, CardHeader, CardContent, Button } from './base'

// 模拟网络统计数据
const networkStats = [
  { label: '节点数', value: '42' },
  { label: '边数', value: '156' },
  { label: '平均度', value: '3.71' },
  { label: '聚类系数', value: '0.68' },
]

export default function NetworkAnalysis() {
  const [selectedNode] = useState<string | null>(null)
  const [layoutType, setLayoutType] = useState<'force' | 'hierarchy'>('force')

  return (
    <div className="p-6 space-y-6">
      {/* 页面标题 */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-slate-900 dark:text-white">网络分析</h1>
          <p className="mt-1 text-sm text-slate-500 dark:text-slate-400">
            因果网络可视化与分析
          </p>
        </div>
        <div className="flex items-center gap-3">
          <Button variant="secondary" leftIcon={<Download className="w-4 h-4" />}>
            导出
          </Button>
          <Button variant="primary" leftIcon={<RefreshCw className="w-4 h-4" />}>
            重新构建
          </Button>
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* 网络可视化 - 占据 2 列 */}
        <Card className="lg:col-span-2">
          <CardHeader 
            title="因果网络图" 
            subtitle="交互式网络可视化"
            action={
              <div className="flex items-center gap-2">
                <Button 
                  variant={layoutType === 'force' ? 'primary' : 'ghost'} 
                  size="sm"
                  onClick={() => setLayoutType('force')}
                >
                  力导向图
                </Button>
                <Button 
                  variant={layoutType === 'hierarchy' ? 'primary' : 'ghost'} 
                  size="sm"
                  onClick={() => setLayoutType('hierarchy')}
                >
                  层次图
                </Button>
              </div>
            }
          />
          <CardContent>
            <div className="h-96 bg-slate-50 dark:bg-slate-700/50 rounded-lg flex items-center justify-center border border-dashed border-slate-300 dark:border-slate-600">
              <div className="text-center">
                <Network className="w-16 h-16 text-slate-400 dark:text-slate-500 mx-auto mb-4" />
                <p className="text-lg font-medium text-slate-600 dark:text-slate-400">D3.js 网络可视化</p>
                <p className="text-sm text-slate-500 dark:text-slate-500 mt-2">点击节点查看详情</p>
                <div className="flex justify-center gap-2 mt-4">
                  <Button variant="primary" size="sm">
                    构建网络
                  </Button>
                  <Button variant="secondary" size="sm">
                    <Settings className="w-4 h-4 mr-2" />
                    设置
                  </Button>
                </div>
              </div>
            </div>
          </CardContent>
        </Card>

        {/* 右侧面板 */}
        <div className="space-y-6">
          {/* 网络统计 */}
          <Card>
            <CardHeader title="网络统计" subtitle="当前网络指标" />
            <CardContent>
              <div className="grid grid-cols-2 gap-4">
                {networkStats.map((stat) => (
                  <div key={stat.label} className="p-3 bg-slate-50 dark:bg-slate-700/50 rounded-lg">
                    <p className="text-xs text-slate-500 dark:text-slate-400">{stat.label}</p>
                    <p className="text-lg font-bold text-slate-900 dark:text-white mt-1">{stat.value}</p>
                  </div>
                ))}
              </div>
            </CardContent>
          </Card>

          {/* 节点详情 */}
          <Card>
            <CardHeader title="节点详情" subtitle="选中节点信息" />
            <CardContent>
              {selectedNode ? (
                <div className="space-y-4">
                  <div className="p-4 bg-primary-50 dark:bg-primary-900/20 rounded-lg border border-primary-200 dark:border-primary-800">
                    <p className="text-sm text-primary-600 dark:text-primary-400">指标名称</p>
                    <p className="font-semibold text-primary-900 dark:text-primary-100">{selectedNode}</p>
                  </div>
                  <div className="grid grid-cols-2 gap-3">
                    <div className="p-3 bg-slate-50 dark:bg-slate-700/50 rounded-lg">
                      <p className="text-xs text-slate-500 dark:text-slate-400">中心性</p>
                      <p className="font-semibold text-slate-900 dark:text-white">0.85</p>
                    </div>
                    <div className="p-3 bg-slate-50 dark:bg-slate-700/50 rounded-lg">
                      <p className="text-xs text-slate-500 dark:text-slate-400">连接数</p>
                      <p className="font-semibold text-slate-900 dark:text-white">12</p>
                    </div>
                    <div className="p-3 bg-slate-50 dark:bg-slate-700/50 rounded-lg">
                      <p className="text-xs text-slate-500 dark:text-slate-400">入度</p>
                      <p className="font-semibold text-slate-900 dark:text-white">5</p>
                    </div>
                    <div className="p-3 bg-slate-50 dark:bg-slate-700/50 rounded-lg">
                      <p className="text-xs text-slate-500 dark:text-slate-400">出度</p>
                      <p className="font-semibold text-slate-900 dark:text-white">7</p>
                    </div>
                  </div>
                  <div className="p-3 bg-slate-50 dark:bg-slate-700/50 rounded-lg">
                    <p className="text-xs text-slate-500 dark:text-slate-400">所属社区</p>
                    <p className="font-semibold text-slate-900 dark:text-white">经济核心</p>
                  </div>
                </div>
              ) : (
                <div className="text-center py-8">
                  <GitBranch className="w-12 h-12 text-slate-400 dark:text-slate-500 mx-auto mb-3" />
                  <p className="text-slate-500 dark:text-slate-400">点击节点查看详情</p>
                </div>
              )}
            </CardContent>
          </Card>

          {/* 图例 */}
          <Card>
            <CardHeader title="图例" subtitle="节点颜色说明" />
            <CardContent>
              <div className="space-y-3">
                {[
                  { color: 'bg-blue-500', label: '经济核心指标' },
                  { color: 'bg-green-500', label: '金融指标' },
                  { color: 'bg-yellow-500', label: '贸易指标' },
                  { color: 'bg-purple-500', label: '社会指标' },
                ].map((item) => (
                  <div key={item.label} className="flex items-center gap-3">
                    <div className={`w-3 h-3 rounded-full ${item.color}`} />
                    <span className="text-sm text-slate-600 dark:text-slate-400">{item.label}</span>
                  </div>
                ))}
              </div>
            </CardContent>
          </Card>
        </div>
      </div>
    </div>
  )
}
