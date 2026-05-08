import { lazy, Suspense } from 'react'
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom'
import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import { ThemeProvider } from './hooks/useTheme'
import { ToastProvider } from './components/base/Toast'
import { ErrorBoundary } from './components/base/ErrorBoundary'
import Header from './components/Header'
import Sidebar from './components/Sidebar'

// 懒加载页面组件
const Dashboard = lazy(() => import('./components/Dashboard'))
const NetworkAnalysis = lazy(() => import('./components/NetworkAnalysis'))
const CausalDiscovery = lazy(() => import('./components/CausalDiscovery'))
const RippleSimulation = lazy(() => import('./components/RippleSimulation'))
const DataSources = lazy(() => import('./components/DataSources'))

// 加载状态组件
function PageLoader() {
  return (
    <div className="flex items-center justify-center h-64">
      <div className="text-center">
        <div className="w-12 h-12 border-4 border-primary-600 border-t-transparent rounded-full animate-spin mx-auto mb-4" />
        <p className="text-slate-500 dark:text-slate-400">加载中...</p>
      </div>
    </div>
  )
}

const queryClient = new QueryClient()

function App() {
  return (
    <ErrorBoundary>
      <ThemeProvider defaultTheme="system">
        <ToastProvider>
          <QueryClientProvider client={queryClient}>
            <Router>
              <div className="flex h-screen bg-slate-50 dark:bg-slate-900 transition-colors duration-200">
                <Sidebar />
                <div className="flex-1 flex flex-col overflow-hidden">
                  <Header />
                  <main className="flex-1 overflow-x-hidden overflow-y-auto bg-slate-50 dark:bg-slate-900">
                    <Suspense fallback={<PageLoader />}>
                      <Routes>
                        <Route path="/" element={<Dashboard />} />
                        <Route path="/network" element={<NetworkAnalysis />} />
                        <Route path="/causal" element={<CausalDiscovery />} />
                        <Route path="/ripple" element={<RippleSimulation />} />
                        <Route path="/data" element={<DataSources />} />
                      </Routes>
                    </Suspense>
                  </main>
                </div>
              </div>
            </Router>
          </QueryClientProvider>
        </ToastProvider>
      </ThemeProvider>
    </ErrorBoundary>
  )
}

export default App
