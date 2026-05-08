// Button 组件测试

import { describe, it, expect, vi } from 'vitest'
import { render, screen, fireEvent } from '@testing-library/react'
import { Button } from '../Button'

describe('Button', () => {
  // 基础渲染测试
  it('renders correctly', () => {
    render(<Button>Click me</Button>)
    expect(screen.getByRole('button', { name: /click me/i })).toBeInTheDocument()
  })

  // 变体测试
  describe('variants', () => {
    it('renders primary variant by default', () => {
      render(<Button>Primary</Button>)
      const button = screen.getByRole('button')
      expect(button).toHaveClass('bg-primary-600')
    })

    it('renders secondary variant', () => {
      render(<Button variant="secondary">Secondary</Button>)
      const button = screen.getByRole('button')
      expect(button).toHaveClass('bg-white')
      expect(button).toHaveClass('text-primary-600')
    })

    it('renders ghost variant', () => {
      render(<Button variant="ghost">Ghost</Button>)
      const button = screen.getByRole('button')
      expect(button).toHaveClass('bg-transparent')
    })

    it('renders danger variant', () => {
      render(<Button variant="danger">Danger</Button>)
      const button = screen.getByRole('button')
      expect(button).toHaveClass('bg-error')
    })

    it('renders success variant', () => {
      render(<Button variant="success">Success</Button>)
      const button = screen.getByRole('button')
      expect(button).toHaveClass('bg-semantic-success')
    })
  })

  // 尺寸测试
  describe('sizes', () => {
    it('renders medium size by default', () => {
      render(<Button>Medium</Button>)
      const button = screen.getByRole('button')
      expect(button).toHaveClass('px-4')
      expect(button).toHaveClass('py-2')
    })

    it('renders small size', () => {
      render(<Button size="sm">Small</Button>)
      const button = screen.getByRole('button')
      expect(button).toHaveClass('px-3')
      expect(button).toHaveClass('py-1.5')
    })

    it('renders large size', () => {
      render(<Button size="lg">Large</Button>)
      const button = screen.getByRole('button')
      expect(button).toHaveClass('px-6')
      expect(button).toHaveClass('py-3')
    })

    it('renders extra large size', () => {
      render(<Button size="xl">XL</Button>)
      const button = screen.getByRole('button')
      expect(button).toHaveClass('px-8')
      expect(button).toHaveClass('py-4')
    })
  })

  // 状态测试
  describe('states', () => {
    it('renders disabled state', () => {
      render(<Button disabled>Disabled</Button>)
      const button = screen.getByRole('button')
      expect(button).toBeDisabled()
      expect(button).toHaveClass('opacity-50')
      expect(button).toHaveClass('cursor-not-allowed')
    })

    it('renders loading state', () => {
      render(<Button isLoading>Loading</Button>)
      const button = screen.getByRole('button')
      expect(button).toBeDisabled()
      expect(button).toHaveClass('cursor-wait')
      expect(screen.getByText('Loading')).toHaveClass('invisible')
    })
  })

  // 交互测试
  describe('interactions', () => {
    it('calls onClick when clicked', () => {
      const handleClick = vi.fn()
      render(<Button onClick={handleClick}>Click me</Button>)
      
      fireEvent.click(screen.getByRole('button'))
      expect(handleClick).toHaveBeenCalledTimes(1)
    })

    it('does not call onClick when disabled', () => {
      const handleClick = vi.fn()
      render(<Button onClick={handleClick} disabled>Disabled</Button>)
      
      fireEvent.click(screen.getByRole('button'))
      expect(handleClick).not.toHaveBeenCalled()
    })

    it('does not call onClick when loading', () => {
      const handleClick = vi.fn()
      render(<Button onClick={handleClick} isLoading>Loading</Button>)
      
      fireEvent.click(screen.getByRole('button'))
      expect(handleClick).not.toHaveBeenCalled()
    })
  })

  // 图标测试
  describe('icons', () => {
    it('renders left icon', () => {
      const icon = <span data-testid="left-icon">←</span>
      render(<Button leftIcon={icon}>With Icon</Button>)
      
      expect(screen.getByTestId('left-icon')).toBeInTheDocument()
      expect(screen.getByTestId('left-icon').parentElement).toHaveClass('mr-2')
    })

    it('renders right icon', () => {
      const icon = <span data-testid="right-icon">→</span>
      render(<Button rightIcon={icon}>With Icon</Button>)
      
      expect(screen.getByTestId('right-icon')).toBeInTheDocument()
      expect(screen.getByTestId('right-icon').parentElement).toHaveClass('ml-2')
    })

    it('hides icons when loading', () => {
      const leftIcon = <span data-testid="left-icon">←</span>
      const rightIcon = <span data-testid="right-icon">→</span>
      render(
        <Button isLoading leftIcon={leftIcon} rightIcon={rightIcon}>
          Loading
        </Button>
      )
      
      expect(screen.queryByTestId('left-icon')).not.toBeInTheDocument()
      expect(screen.queryByTestId('right-icon')).not.toBeInTheDocument()
    })
  })

  // 全宽测试
  describe('fullWidth', () => {
    it('renders full width when fullWidth is true', () => {
      render(<Button fullWidth>Full Width</Button>)
      const button = screen.getByRole('button')
      expect(button).toHaveClass('w-full')
    })
  })

  // 自定义类名测试
  describe('custom className', () => {
    it('applies custom className', () => {
      render(<Button className="custom-class">Custom</Button>)
      const button = screen.getByRole('button')
      expect(button).toHaveClass('custom-class')
    })
  })

  // 可访问性测试
  describe('accessibility', () => {
    it('has correct role', () => {
      render(<Button>Accessible</Button>)
      expect(screen.getByRole('button')).toBeInTheDocument()
    })

    it('supports aria attributes', () => {
      render(
        <Button aria-label="Custom label" aria-describedby="description">
          With ARIA
        </Button>
      )
      
      const button = screen.getByRole('button', { name: /custom label/i })
      expect(button).toHaveAttribute('aria-describedby', 'description')
    })
  })
})
