// 涟漪模拟页面
// 使用新组件重构，展示涟漪效应模拟功能

import { useState } from 'react'
import { Waves, Play, Download, Settings, BarChart3, TrendingUp, TrendingDown } from 'lucide-react'
import { Card, CardHeader, CardContent, Button } from './base'

// 经济指标选项
const indicators = [
  { id: 'gdp', name: 'GDP', category: '产出' },
  { id: 'cpi', name: 'CPI', category: '价格' },
  { id: 'pmi', name: 'PMI', category: '产出' },
  { id: 'ppi', name: 'PPI', category: '价格' },
  { id: 'm2', name: 'M2', category: '货币' },
  { id: 'shibor', name: 'SHIBOR', category: '利率' },
  { id: 'shanghai', name: '上证指数', category: '金融' },
  { id: 'shenzhen', name: '深证成指', category: '金融' },
  { id: 'chinext', name: '创业板指', category: '金融' },
]

// 模拟结果数据
const mockImpactResults = [
  { name: 'CPI', impact: 0.85, direction: 'up' },
  { name: 'PMI', impact: 0.62, direction: 'up' },
  { name: 'M2', impact: 0.45, direction: 'up' },
  { name: '利率', impact: 0.38, direction: 'down' },
  { name: '投资', impact: 0.32, direction: 'up' },
]

export default function RippleSimulation() {
  const [selectedIndicator, setSelectedIndicator] = useState('gdp')
  const [shockMagnitude, setShockMagnitude] = useState(10)
  const [decayFactor, setDecayFactor] = useState(0.5)
  const [isSimulating, setIsSimulating] = useState(false)
  const [showResults, setShowResults] = useState(false)

  const handleSimulate = () => {
    setIsSimulating(true)
    setShowResults(false)
    // 模拟分析过程
    setTimeout(() => {
      setIsSimulating(false)
      setShowResults(true)
    }, 3000)
  }

  return (
    <div className="p-6 space-y-6">
      {/* 页面标题 */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-slate-900 dark:text-white">涟漪效应模拟</h1>
          <p className="mt-1 text-sm text-slate-500 dark:text-slate-400">
            模拟指标波动对经济网络的传导效应
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
        {/* 模拟参数控制 */}
        <Card>
          <CardHeader title="模拟参数" subtitle="配置冲击参数" />
          <CardContent>
            <div className="space-y-5">
              {/* 冲击指标选择 */}
              <div>
                <label className="block text-sm font-medium text-slate-700 dark:text-slate-300 mb-2">
                  冲击指标
                </label>
                <select
                  value={selectedIndicator}
                  onChange={(e) => setSelectedIndicator(e.target.value)}
                  className="w-full px-4 py-2.5 bg-slate-50 dark:bg-slate-700 border border-slate-200 dark:border-slate-600 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500 focus:border-transparent text-slate-900 dark:text-white"
                >
                  {indicators.map((ind) => (
                    <option key={ind.id} value={ind.id}>
                      {ind.name} ({ind.category})
                    </option>
                  ))}
                </select>
              </div>

              {/* 冲击幅度 */}
              <div>
                <div className="flex items-center justify-between mb-2">
                  <label className="text-sm font-medium text-slate-700 dark:text-slate-300">
                    冲击幅度
                  </label>
                  <span className={`text-sm font-semibold ${shockMagnitude >= 0 ? 'text-green-600 dark:text-green-400' : 'text-red-600 dark:text-red-400'}`}>
                    {shockMagnitude > 0 ? '+' : ''}{shockMagnitude}%
                  </span>
                </div>
                <input
                  type="range"
                  min="-50"
                  max="50"
                  value={shockMagnitude}
                  onChange={(e) => setShockMagnitude(Number(e.target.value))}
                  className="w-full h-2 bg-slate-200 dark:bg-slate-600 rounded-lg appearance-none cursor-pointer accent-primary-600"
                />
                <div className="flex justify-between mt-1">
                  <span className="text-xs text-slate-500 dark:text-slate-400">-50%</span>
                  <span className="text-xs text-slate-500 dark:text-slate-400">0%</span>
                  <span className="text-xs text-slate-500 dark:text-slate-400">+50%</span>
                </div>
              </div>

              {/* 衰减因子 */}
              <div>
                <div className="flex items-center justify-between mb-2">
                  <label className="text-sm font-medium text-slate-700 dark:text-slate-300">
                    衰减因子
                  </label>
                  <span className="text-sm font-semibold text-slate-900 dark:text-white">
                    {decayFactor.toFixed(1)}
                  </span>
                </div>
                <input
                  type="range"
                  min="0"
                  max="1"
                  step="0.1"
                  value={decayFactor}
                  onChange={(e) => setDecayFactor(Number(e.target.value))}
                  className="w-full h-2 bg-slate-200 dark:bg-slate-600 rounded-lg appearance-none cursor-pointer accent-primary-600"
                />
                <div className="flex justify-between mt-1">
                  <span className="text-xs text-slate-500 dark:text-slate-400">快速衰减</span>
                  <span className="text-xs text-slate-500 dark:text-slate-400">慢速衰减</span>
                </div>
              </div>
            </div>

            <Button 
              variant="primary" 
              fullWidth 
              className="mt-6"
              onClick={handleSimulate}
              disabled={isSimulating}
              isLoading={isSimulating}
              leftIcon={!isSimulating ? <Play className="w-4 h-4" /> : undefined}
            >
              {isSimulating ? '模拟中...' : '开始模拟'}
            </Button>
          </CardContent>
        </Card>

        {/* 模拟结果 */}
        <Card className="lg:col-span-2">
          <CardHeader 
            title="模拟结果" 
            subtitle="冲击传导效应"
            action={
              showResults && (
                <Button variant="ghost" size="sm" leftIcon={<BarChart3 className="w-4 h-4" />}>
                  查看详情
                </Button>
              )
            }
          />
          <CardContent>
            {isSimulating ? (
              <div className="h-96 flex items-center justify-center">
                <div className="text-center">
                  <div className="w-16 h-16 border-4 border-primary-600 border-t-transparent rounded-full animate-spin mx-auto mb-4" />
                  <p className="text-lg font-medium text-slate-600 dark:text-slate-400">正在执行涟漪模拟...</p>
                  <p className="text-sm text-slate-500 dark:text-slate-500 mt-2">
                    计算 {indicators.find(i => i.id === selectedIndicator)?.name} 的冲击传导
                  </p>
                </div>
              </div>
            ) : showResults ? (
              <div className="space-y-6">
                {/* 冲击概览 */}
                <div className="grid grid-cols-3 gap-4">
                  <div className="p-4 bg-primary-50 dark:bg-primary-900/20 rounded-lg border border-primary-200 dark:border-primary-800">
                    <p className="text-sm text-primary-600 dark:text-primary-400">冲击指标</p>
                    <p className="text-xl font-bold text-primary-700 dark:text-primary-300 mt-1">
                      {indicators.find(i => i.id === selectedIndicator)?.name}
                    </p>
                  </div>
                  <div className="p-4 bg-slate-50 dark:bg-slate-700/50 rounded-lg border border-slate-200 dark:border-slate-600">
                    <p className="text-sm text-slate-500 dark:text-slate-400">冲击幅度</p>
                    <p className={`text-xl font-bold mt-1 ${shockMagnitude >= 0 ? 'text-green-600 dark:text-green-400' : 'text-red-600 dark:text-red-400'}`}>
                      {shockMagnitude > 0 ? '+' : ''}{shockMagnitude}%
                    </p>
                  </div>
                  <div className="p-4 bg-slate-50 dark:bg-slate-700/50 rounded-lg border border-slate-200 dark:border-slate-600">
                    <p className="text-sm text-slate-500 dark:text-slate-400">衰减因子</p>
                    <p className="text-xl font-bold text-slate-900 dark:text-white mt-1">{decayFactor.toFixed(1)}</p>
                  </div>
                </div>

                {/* 受影响指标列表 */}
                <div>
                  <h3 className="text-sm font-medium text-slate-700 dark:text-slate-300 mb-3">受影响指标</h3>
                  <div className="space-y-3">
                    {mockImpactResults.map((result, index) => (
                      <div 
                        key={index}
                        className="flex items-center justify-between p-4 bg-slate-50 dark:bg-slate-700/50 rounded-lg"
                      >
                        <div className="flex items-center gap-4">
                          <div className={`w-10 h-10 rounded-lg flex items-center justify-center ${
                            result.direction === 'up' 
                              ? 'bg-green-100 dark:bg-green-900/30' 
                              : 'bg-red-100 dark:bg-red-900/30'
                          }`}>
                            {result.direction === 'up' ? (
                              <TrendingUp className="w-5 h-5 text-green-600 dark:text-green-400" />
                            ) : (
                              <TrendingDown className="w-5 h-5 text-red-600 dark:text-red-400" />
                            )}
                          </div>
                          <div>
                            <p className="font-medium text-slate-900 dark:text-white">{result.name}</p>
                            <p className="text-sm text-slate-500 dark:text-slate-400">
                              {result.direction === 'up' ? '正向影响' : '负向影响'}
                            </p>
                          </div>
                        </div>
                        <div className="flex items-center gap-4">
                          <div className="text-right">
                            <p className="text-sm text-slate-500 dark:text-slate-400">影响程度</p>
                            <p className={`font-semibold ${result.direction === 'up' ? 'text-green-600 dark:text-green-400' : 'text-red-600 dark:text-red-400'}`}>
                              {(result.impact * 100).toFixed(0)}%
                            </p>
                          </div>
                          <div className="w-20 h-2 bg-slate-200 dark:bg-slate-600 rounded-full overflow-hidden">
                            <div 
                              className={`h-full rounded-full ${result.direction === 'up' ? 'bg-green-500' : 'bg-red-500'}`}
                              style={{ width: `${result.impact * 100}%` }}
                            />
                          </div>
                        </div>
                      </div>
                    ))}
                  </div>
                </div>
              </div>
            ) : (
              <div className="h-96 flex items-center justify-center">
                <div className="text-center">
                  <Waves className="w-16 h-16 text-slate-400 dark:text-slate-500 mx-auto mb-4" />
                  <p className="text-lg font-medium text-slate-600 dark:text-slate-400">等待模拟</p>
                  <p className="text-sm text-slate-500 dark:text-slate-500 mt-2">配置参数并点击"开始模拟"</p>
                </div>
              </div>
            )}
          </CardContent>
        </Card>
      </div>
    </div>
  )
}
