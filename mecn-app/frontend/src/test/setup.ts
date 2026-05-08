// 测试设置文件
// 配置测试环境和全局工具

import '@testing-library/jest-dom'

// 模拟 matchMedia
Object.defineProperty(window, 'matchMedia', {
  writable: true,
  value: vi.fn().mockImplementation((query: string) => ({
    matches: false,
    media: query,
    onchange: null,
    addListener: vi.fn(),
    removeListener: vi.fn(),
    addEventListener: vi.fn(),
    removeEventListener: vi.fn(),
    dispatchEvent: vi.fn(),
  })),
})

// 模拟 ResizeObserver
class ResizeObserverMock {
  observe = vi.fn()
  unobserve = vi.fn()
  disconnect = vi.fn()
}

window.ResizeObserver = ResizeObserverMock as unknown as typeof ResizeObserver

// 模拟 IntersectionObserver
class IntersectionObserverMock {
  root = null
  rootMargin = ''
  thresholds = [0]
  
  constructor(callback: IntersectionObserverCallback) {
    this.callback = callback
  }
  
  callback: IntersectionObserverCallback
  
  observe = vi.fn()
  unobserve = vi.fn()
  disconnect = vi.fn()
}

window.IntersectionObserver = IntersectionObserverMock as unknown as typeof IntersectionObserver

// 模拟 scrollTo
window.scrollTo = vi.fn()

// 模拟 localStorage
const localStorageMock = (() => {
  let store: Record<string, string> = {}
  
  return {
    getItem: (key: string) => store[key] || null,
    setItem: (key: string, value: string) => {
      store[key] = value
    },
    removeItem: (key: string) => {
      delete store[key]
    },
    clear: () => {
      store = {}
    },
  }
})()

Object.defineProperty(window, 'localStorage', {
  value: localStorageMock,
})

// 模拟 URL.createObjectURL
URL.createObjectURL = vi.fn(() => 'blob://localhost/test')
URL.revokeObjectURL = vi.fn()

// 模拟 window.getComputedStyle
window.getComputedStyle = vi.fn(() => ({
  getPropertyValue: vi.fn(() => ''),
  alignItems: '',
  backgroundColor: '',
  color: '',
  display: '',
  flexDirection: '',
  fontSize: '',
  fontWeight: '',
  height: '',
  justifyContent: '',
  marginTop: '',
  maxWidth: '',
  opacity: '',
  padding: '',
  position: '',
  textAlign: '',
  width: '',
}))

// 清理每个测试后的 DOM
afterEach(() => {
  // 清理所有 mock
  vi.clearAllMocks()
})

// 设置测试超时
vi.setConfig({ testTimeout: 10000 })
