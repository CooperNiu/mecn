// 动画工具函数和样式
// 提供统一的动画效果

// 渐入动画
export const fadeIn = {
  initial: { opacity: 0 },
  animate: { opacity: 1 },
  exit: { opacity: 0 },
  transition: { duration: 0.2 },
}

// 从底部滑入
export const slideUp = {
  initial: { opacity: 0, y: 20 },
  animate: { opacity: 1, y: 0 },
  exit: { opacity: 0, y: 20 },
  transition: { duration: 0.3, ease: 'easeOut' },
}

// 从右侧滑入
export const slideInRight = {
  initial: { opacity: 0, x: 20 },
  animate: { opacity: 1, x: 0 },
  exit: { opacity: 0, x: 20 },
  transition: { duration: 0.3, ease: 'easeOut' },
}

// 缩放动画
export const scale = {
  initial: { opacity: 0, scale: 0.95 },
  animate: { opacity: 1, scale: 1 },
  exit: { opacity: 0, scale: 0.95 },
  transition: { duration: 0.2, ease: 'easeOut' },
}

// Tailwind CSS 动画类
export const tailwindAnimations = {
  // 渐入
  fadeIn: 'animate-fade-in',
  // 滑入
  slideUp: 'animate-slide-up',
  slideDown: 'animate-slide-down',
  slideLeft: 'animate-slide-left',
  slideRight: 'animate-slide-right',
  // 缩放
  scaleIn: 'animate-scale-in',
  // 脉冲
  pulse: 'animate-pulse',
  // 旋转
  spin: 'animate-spin',
  // 弹跳
  bounce: 'animate-bounce',
}

// 悬停效果
export const hoverEffects = {
  // 抬升
  lift: 'hover:-translate-y-0.5 hover:shadow-md transition-all duration-200',
  // 缩放
  scale: 'hover:scale-[1.02] transition-transform duration-200',
  // 背景变化
  background: 'hover:bg-slate-100 dark:hover:bg-slate-700 transition-colors duration-200',
  // 边框变化
  border: 'hover:border-primary-300 dark:hover:border-primary-600 transition-colors duration-200',
  // 光泽效果
  glow: 'hover:shadow-lg hover:shadow-primary-500/20 transition-shadow duration-200',
}

// 焦点效果
export const focusEffects = {
  // 基础焦点
  base: 'focus:outline-none focus:ring-2 focus:ring-primary-500 focus:ring-offset-2 dark:focus:ring-offset-slate-800',
  // 内部焦点
  inner: 'focus:outline-none focus:ring-2 focus:ring-inset focus:ring-primary-500',
  // 虚线焦点
  dashed: 'focus:outline-none focus:ring-2 focus:ring-dashed focus:ring-primary-500',
}

// 过渡效果
export const transitions = {
  // 基础过渡
  base: 'transition-all duration-200 ease-in-out',
  // 快速过渡
  fast: 'transition-all duration-100 ease-in-out',
  // 慢速过渡
  slow: 'transition-all duration-300 ease-in-out',
  // 颜色过渡
  colors: 'transition-colors duration-200',
  // 阴影过渡
  shadow: 'transition-shadow duration-200',
  // 变换过渡
  transform: 'transition-transform duration-200',
}

// 加载动画
export const loadingAnimations = {
  // 旋转
  spin: 'animate-spin',
  // 脉冲
  pulse: 'animate-pulse',
  // 弹跳
  bounce: 'animate-bounce',
  // 摆动
  wiggle: 'animate-wiggle',
}

// 骨架屏动画
export const skeletonAnimation = 'animate-pulse bg-slate-200 dark:bg-slate-700 rounded'

// 进度条动画
export const progressBarAnimation = 'transition-all duration-500 ease-out'

// 页面过渡
export const pageTransition = {
  initial: { opacity: 0, y: 10 },
  animate: { opacity: 1, y: 0 },
  exit: { opacity: 0, y: -10 },
  transition: { duration: 0.2, ease: 'easeInOut' },
}

// 模态框动画
export const modalAnimation = {
  overlay: {
    initial: { opacity: 0 },
    animate: { opacity: 1 },
    exit: { opacity: 0 },
  },
  content: {
    initial: { opacity: 0, scale: 0.95, y: 10 },
    animate: { opacity: 1, scale: 1, y: 0 },
    exit: { opacity: 0, scale: 0.95, y: 10 },
  },
  transition: { duration: 0.2, ease: 'easeOut' },
}

// Toast 动画
export const toastAnimation = {
  initial: { opacity: 0, x: 100, scale: 0.95 },
  animate: { opacity: 1, x: 0, scale: 1 },
  exit: { opacity: 0, x: 100, scale: 0.95 },
  transition: { duration: 0.3, ease: 'easeOut' },
}

// 列表动画
export const listAnimation = {
  initial: { opacity: 0 },
  animate: { opacity: 1 },
  transition: { staggerChildren: 0.05 },
}

// 列表项动画
export const listItemAnimation = {
  initial: { opacity: 0, y: 10 },
  animate: { opacity: 1, y: 0 },
  transition: { duration: 0.2 },
}

// 数字动画（用于计数器）
export const numberAnimation = {
  initial: { opacity: 0, y: -10 },
  animate: { opacity: 1, y: 0 },
  transition: { duration: 0.3, ease: 'easeOut' },
}

// 图标动画
export const iconAnimation = {
  // 旋转
  spin: 'animate-spin',
  // 脉冲
  pulse: 'animate-pulse',
  // 弹跳
  bounce: 'animate-bounce',
  // 摇晃
  shake: 'animate-shake',
}

// 按钮点击效果
export const buttonClickEffect = 'active:scale-[0.98] transition-transform duration-100'

// 卡片悬停效果
export const cardHoverEffect = 'hover:shadow-md hover:border-slate-300 dark:hover:border-slate-600 transition-all duration-200'

// 链接悬停效果
export const linkHoverEffect = 'hover:text-primary-600 dark:hover:text-primary-400 transition-colors duration-200'

// 表格行悬停效果
export const tableRowHoverEffect = 'hover:bg-slate-50 dark:hover:bg-slate-700/50 transition-colors duration-150'

// 输入框焦点效果
export const inputFocusEffect = 'focus:ring-2 focus:ring-primary-500 focus:border-primary-500 transition-all duration-200'

// 标签页切换动画
export const tabSwitchAnimation = {
  initial: { opacity: 0, y: 5 },
  animate: { opacity: 1, y: 0 },
  transition: { duration: 0.2 },
}

// 折叠展开动画
export const collapseAnimation = {
  initial: { height: 0, opacity: 0 },
  animate: { height: 'auto', opacity: 1 },
  exit: { height: 0, opacity: 0 },
  transition: { duration: 0.3, ease: 'easeInOut' },
}

// 下拉菜单动画
export const dropdownAnimation = {
  initial: { opacity: 0, y: -10, scale: 0.95 },
  animate: { opacity: 1, y: 0, scale: 1 },
  exit: { opacity: 0, y: -10, scale: 0.95 },
  transition: { duration: 0.15, ease: 'easeOut' },
}

// 工具提示动画
export const tooltipAnimation = {
  initial: { opacity: 0, y: 5 },
  animate: { opacity: 1, y: 0 },
  exit: { opacity: 0, y: 5 },
  transition: { duration: 0.15 },
}

// 通知动画
export const notificationAnimation = {
  initial: { opacity: 0, x: 100 },
  animate: { opacity: 1, x: 0 },
  exit: { opacity: 0, x: 100 },
  transition: { duration: 0.3, ease: 'easeOut' },
}

// 侧边栏折叠动画
export const sidebarCollapseAnimation = {
  transition: 'transition-all duration-300 ease-in-out',
}

// 主题切换动画
export const themeSwitchAnimation = {
  transition: 'transition-colors duration-300 ease-in-out',
}

// 滚动动画
export const scrollAnimation = {
  // 滚动到顶部
  scrollToTop: 'scroll-smooth',
  // 滚动行为
  behavior: 'smooth',
}

// 视差效果
export const parallaxEffect = {
  // 视差滚动
  parallax: 'transform-gpu will-change-transform',
}

// 模糊效果
export const blurEffect = {
  // 背景模糊
  backdrop: 'backdrop-blur-sm',
  // 强模糊
  strong: 'backdrop-blur-md',
  // 强烈模糊
  stronger: 'backdrop-blur-lg',
}

// 渐变效果
export const gradientEffects = {
  // 线性渐变
  linear: 'bg-gradient-to-r',
  // 径向渐变
  radial: 'bg-gradient-to-br',
  // 渐变文字
  text: 'bg-clip-text text-transparent bg-gradient-to-r',
}

// 阴影效果
export const shadowEffects = {
  // 内阴影
  inner: 'shadow-inner',
  // 外阴影
  outer: 'shadow-lg',
  // 彩色阴影
  colored: 'shadow-lg shadow-primary-500/25',
}

// 边框效果
export const borderEffects = {
  // 渐变边框
  gradient: 'border-2 border-transparent bg-clip-padding',
  // 双边框
  double: 'border-2 border-double',
  // 虚线边框
  dashed: 'border-2 border-dashed',
}

// 背景效果
export const backgroundEffects = {
  // 网格背景
  grid: 'bg-grid-slate-200 dark:bg-grid-slate-700',
  // 点状背景
  dots: 'bg-dot-slate-200 dark:bg-dot-slate-700',
  // 条纹背景
  stripes: 'bg-stripes-slate-200 dark:bg-stripes-slate-700',
}

// 文字效果
export const textEffects = {
  // 渐变文字
  gradient: 'bg-clip-text text-transparent bg-gradient-to-r from-primary-600 to-primary-400',
  // 阴影文字
  shadow: 'text-shadow-lg',
  // 轮廓文字
  outline: 'text-outline',
}

// 交互效果
export const interactionEffects = {
  // 禁用效果
  disabled: 'disabled:opacity-50 disabled:cursor-not-allowed disabled:pointer-events-none',
  // 只读效果
  readonly: 'read-only:bg-slate-50 read-only:cursor-not-allowed',
  // 占位符效果
  placeholder: 'placeholder:text-slate-400 dark:placeholder:text-slate-500',
}

// 响应式效果
export const responsiveEffects = {
  // 移动端隐藏
  hideOnMobile: 'hidden sm:block',
  // 桌面端隐藏
  hideOnDesktop: 'block sm:hidden',
  // 移动端全宽
  fullOnMobile: 'w-full sm:w-auto',
}

// 打印效果
export const printEffects = {
  // 打印时隐藏
  hideOnPrint: 'print:hidden',
  // 打印时显示
  showOnPrint: 'hidden print:block',
}

// 可访问性效果
export const accessibilityEffects = {
  // 屏幕阅读器隐藏
  srOnly: 'sr-only',
  // 焦点可见
  focusVisible: 'focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-primary-500',
  // 减少动画
  reduceMotion: 'motion-reduce:transition-none motion-reduce:animate-none',
}

// 性能优化效果
export const performanceEffects = {
  // GPU 加速
  gpu: 'transform-gpu',
  // will-change
  willChange: 'will-change-transform',
  // 内容可见性
  contentVisibility: 'content-visibility-auto',
}

// 综合动画样式
export const comprehensiveAnimations = {
  fadeIn,
  slideUp,
  slideInRight,
  scale,
  tailwindAnimations,
  hoverEffects,
  focusEffects,
  transitions,
  loadingAnimations,
  skeletonAnimation,
  progressBarAnimation,
  pageTransition,
  modalAnimation,
  toastAnimation,
  listAnimation,
  listItemAnimation,
  numberAnimation,
  iconAnimation,
  buttonClickEffect,
  cardHoverEffect,
  linkHoverEffect,
  tableRowHoverEffect,
  inputFocusEffect,
  tabSwitchAnimation,
  collapseAnimation,
  dropdownAnimation,
  tooltipAnimation,
  notificationAnimation,
  sidebarCollapseAnimation,
  themeSwitchAnimation,
  scrollAnimation,
  parallaxEffect,
  blurEffect,
  gradientEffects,
  shadowEffects,
  borderEffects,
  backgroundEffects,
  textEffects,
  interactionEffects,
  responsiveEffects,
  printEffects,
  accessibilityEffects,
  performanceEffects,
}

export default comprehensiveAnimations
