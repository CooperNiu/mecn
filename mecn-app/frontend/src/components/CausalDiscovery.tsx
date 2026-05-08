// 因果发现页面
// 使用新组件重构，展示因果发现功能

import { useState } from 'react'
import { GitBranch, Play, Download, Settings, BarChart3, Network } from 'lucide-react'
import { Card, CardHeader, CardContent, Button } from './base'

// 算法选项
const methods = [
  { 
    id: 'lasso', 
    name: 'LASSO 回归', 
    description: 'L1 正则化识别稀疏因果结构',
    icon: '📊'
  },
  { 
    id: 'granger', 
    name: 'Granger 因果检验', 
    description: '基于 VAR 模型的 F 统计量检验',
    icon: '📈'
  },
  { 
    id: 'pcmci', 
    name: 'PCMCI 算法', 
    description: 'PC 阶段 + MCI 阶段的先进因果发现',
    icon: '🔬'
  },
  { 
    id: 'ensemble', 
    name: '集成融合', 
    description: '投票机制、置信度加权、混合策略',
    icon: '🎯'
  },
]

// 模拟结果数据
const mockResults = [
  { from: 'GDP', to: 'CPI', strength: 0.85, confidence: 0.92 },
  { from: '利率', to: '投资', strength: 0.72, confidence: 0.88 },
  { from: '就业', to: '消费', strength: 0.68, confidence: 0.85 },
  { from: '出口', to: 'GDP', strength: 0.65, confidence: 0.82 },
]

export default function CausalDiscovery() {
  const [selectedMethod, setSelectedMethod] = useState('ensemble')
  const [isRunning, setIsRunning] = useState(false)
  const [showResults, setShowResults] = useState(false)

  const handleRun = () => {
    setIsRunning(true)
    setShowResults(false)
    // 模拟分析过程
    setTimeout(() => {
      setIsRunning(false)
      setShowResults(true)
    }, 3000)
  }

  return (
    <div className="p-6 space-y-6">
      {/* 页面标题 */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-slate-900 dark:text-white">因果发现</h1>
          <p className="mt-1 text-sm text-slate-500 dark:text-slate-400">
            挖掘经济指标间的因果关系
          </p>
        </div>
        <div className="flex items-center gap-3">
          <Button variant="secondary" leftIcon={<Download className="w-4 h-4" />}>
            导出结果
          </Button>
          <Button variant="secondary" leftIcon={<Settings className="w-4 h-4" />}>
            高级设置
          </Button>
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* 算法选择 */}
        <Card>
          <CardHeader title="选择算法" subtitle="选择因果发现方法" />
          <CardContent>
            <div className="space-y-3">
              {methods.map((method) => (
                <button
                  key={method.id}
                  onClick={() => setSelectedMethod(method.id)}
                  className={`w-full text-left p-4 rounded-xl border transition-all duration-200 ${
                    selectedMethod === method.id
                      ? 'border-primary-500 bg-primary-50 dark:bg-primary-900/20 shadow-sm'
                      : 'border-slate-200 dark:border-slate-700 hover:border-slate-300 dark:hover:border-slate-600 hover:bg-slate-50 dark:hover:bg-slate-700/50'
                  }`}
                >
                  <div className="flex items-center gap-3">
                    <span className="text-2xl">{method.icon}</span>
                    <div>
                      <p className={`font-medium ${selectedMethod === method.id ? 'text-primary-700 dark:text-primary-300' : 'text-slate-900 dark:text-white'}`}>
                        {method.name}
                      </p>
                      <p className="text-sm text-slate-500 dark:text-slate-400 mt-1">
                        {method.description}
                      </p>
                    </div>
                  </div>
                </button>
              ))}
            </div>
            
            <Button 
              variant="primary" 
              fullWidth 
              className="mt-6"
              onClick={handleRun}
              disabled={isRunning}
              isLoading={isRunning}
              leftIcon={!isRunning ? <Play className="w-4 h-4" /> : undefined}
            >
              {isRunning ? '分析中...' : '开始分析'}
            </Button>
          </CardContent>
        </Card>

        {/* 分析结果 */}
        <Card className="lg:col-span-2">
          <CardHeader 
            title="分析结果" 
            subtitle="因果关系发现"
            action={
              showResults && (
                <Button variant="ghost" size="sm" leftIcon={<BarChart3 className="w-4 h-4" />}>
                  查看详情
                </Button>
              )
            }
          />
          <CardContent>
            {isRunning ? (
              <div className="h-96 flex items-center justify-center">
                <div className="text-center">
                  <div className="w-16 h-16 border-4 border-primary-600 border-t-transparent rounded-full animate-spin mx-auto mb-4" />
                  <p className="text-lg font-medium text-slate-600 dark:text-slate-400">正在执行因果发现分析...</p>
                  <p className="text-sm text-slate-500 dark:text-slate-500 mt-2">使用 {methods.find(m => m.id === selectedMethod)?.name} 算法</p>
                </div>
              </div>
            ) : showResults ? (
              <div className="space-y-4">
                {/* 结果统计 */}
                <div className="grid grid-cols-3 gap-4 mb-6">
                  <div className="p-4 bg-green-50 dark:bg-green-900/20 rounded-lg border border-green-200 dark:border-green-800">
                    <p className="text-sm text-green-600 dark:text-green-400">发现因果关系</p>
                    <p className="text-2xl font-bold text-green-700 dark:text-green-300 mt-1">{mockResults.length}</p>
                  </div>
                  <div className="p-4 bg-blue-50 dark:bg-blue-900/20 rounded-lg border border-blue-200 dark:border-blue-800">
                    <p className="text-sm text-blue-600 dark:text-blue-400">平均置信度</p>
                    <p className="text-2xl font-bold text-blue-700 dark:text-blue-300 mt-1">86.7%</p>
                  </div>
                  <div className="p-4 bg-purple-50 dark:bg-purple-900/20 rounded-lg border border-purple-200 dark:border-purple-800">
                    <p className="text-sm text-purple-600 dark:text-purple-400">网络密度</p>
                    <p className="text-2xl font-bold text-purple-700 dark:text-purple-300 mt-1">0.35</p>
                  </div>
                </div>

                {/* 因果关系列表 */}
                <div className="space-y-3">
                  {mockResults.map((result, index) => (
                    <div 
                      key={index}
                      className="flex items-center justify-between p-4 bg-slate-50 dark:bg-slate-700/50 rounded-lg"
                    >
                      <div className="flex items-center gap-4">
                        <div className="w-10 h-10 bg-primary-100 dark:bg-primary-900/30 rounded-lg flex items-center justify-center">
                          <GitBranch className="w-5 h-5 text-primary-600 dark:text-primary-400" />
                        </div>
                        <div>
                          <p className="font-medium text-slate-900 dark:text-white">
                            {result.from} → {result.to}
                          </p>
                          <p className="text-sm text-slate-500 dark:text-slate-400">
                            因果强度: {(result.strength * 100).toFixed(0)}%
                          </p>
                        </div>
                      </div>
                      <div className="flex items-center gap-4">
                        <div className="text-right">
                          <p className="text-sm text-slate-500 dark:text-slate-400">置信度</p>
                          <p className="font-semibold text-slate-900 dark:text-white">{(result.confidence * 100).toFixed(0)}%</p>
                        </div>
                        <div className="w-16 h-2 bg-slate-200 dark:bg-slate-600 rounded-full overflow-hidden">
                          <div 
                            className="h-full bg-primary-500 rounded-full"
                            style={{ width: `${result.confidence * 100}%` }}
                          />
                        </div>
                      </div>
                    </div>
                  ))}
                </div>
              </div>
            ) : (
              <div className="h-96 flex items-center justify-center">
                <div className="text-center">
                  <Network className="w-16 h-16 text-slate-400 dark:text-slate-500 mx-auto mb-4" />
                  <p className="text-lg font-medium text-slate-600 dark:text-slate-400">等待分析</p>
                  <p className="text-sm text-slate-500 dark:text-slate-500 mt-2">选择算法并点击"开始分析"</p>
                </div>
              </div>
            )}
          </CardContent>
        </Card>
      </div>
    </div>
  )
}
