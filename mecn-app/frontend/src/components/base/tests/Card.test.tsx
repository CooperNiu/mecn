// Card 组件测试

import { describe, it, expect, vi } from 'vitest'
import { render, screen, fireEvent } from '@testing-library/react'
import { Card, CardHeader, CardContent, CardFooter } from '../Card'

describe('Card', () => {
  // 基础渲染测试
  it('renders correctly', () => {
    render(<Card>Card content</Card>)
    expect(screen.getByText('Card content')).toBeInTheDocument()
  })

  // 样式测试
  describe('styles', () => {
    it('has base styles', () => {
      render(<Card>Content</Card>)
      const card = screen.getByText('Content').parentElement
      expect(card).toHaveClass('bg-white')
      expect(card).toHaveClass('rounded-xl')
      expect(card).toHaveClass('shadow-sm')
    })

    it('has border by default', () => {
      render(<Card>Content</Card>)
      const card = screen.getByText('Content').parentElement
      expect(card).toHaveClass('border')
      expect(card).toHaveClass('border-slate-200')
    })

    it('removes border when bordered is false', () => {
      render(<Card bordered={false}>Content</Card>)
      const card = screen.getByText('Content').parentElement
      expect(card).not.toHaveClass('border')
    })

    it('has padding by default', () => {
      render(<Card>Content</Card>)
      const card = screen.getByText('Content').parentElement
      expect(card).toHaveClass('p-6')
    })

    it('removes padding when padded is false', () => {
      render(<Card padded={false}>Content</Card>)
      const card = screen.getByText('Content').parentElement
      expect(card).not.toHaveClass('p-6')
    })
  })

  // 交互测试
  describe('interactions', () => {
    it('has hover effect when hover is true', () => {
      render(<Card hover>Content</Card>)
      const card = screen.getByText('Content').parentElement
      expect(card).toHaveClass('hover:shadow-md')
    })

    it('is clickable when clickable is true', () => {
      const handleClick = vi.fn()
      render(<Card clickable onClick={handleClick}>Clickable</Card>)
      
      fireEvent.click(screen.getByText('Clickable'))
      expect(handleClick).toHaveBeenCalledTimes(1)
    })

    it('has cursor pointer when clickable', () => {
      render(<Card clickable>Content</Card>)
      const card = screen.getByText('Content').parentElement
      expect(card).toHaveClass('cursor-pointer')
    })
  })

  // 自定义类名测试
  it('applies custom className', () => {
    render(<Card className="custom-class">Content</Card>)
    const card = screen.getByText('Content').parentElement
    expect(card).toHaveClass('custom-class')
  })
})

describe('CardHeader', () => {
  it('renders title', () => {
    render(<CardHeader title="Header Title" />)
    expect(screen.getByText('Header Title')).toBeInTheDocument()
  })

  it('renders subtitle', () => {
    render(<CardHeader title="Title" subtitle="Subtitle" />)
    expect(screen.getByText('Subtitle')).toBeInTheDocument()
  })

  it('renders action', () => {
    render(
      <CardHeader 
        title="Title" 
        action={<button>Action</button>} 
      />
    )
    expect(screen.getByText('Action')).toBeInTheDocument()
  })

  it('applies custom className', () => {
    render(<CardHeader title="Title" className="custom" />)
    const header = screen.getByText('Title').parentElement?.parentElement
    expect(header).toHaveClass('custom')
  })
})

describe('CardContent', () => {
  it('renders children', () => {
    render(<CardContent>Content here</CardContent>)
    expect(screen.getByText('Content here')).toBeInTheDocument()
  })

  it('has padding by default', () => {
    render(<CardContent>Content</CardContent>)
    const content = screen.getByText('Content').parentElement
    expect(content).toHaveClass('p-6')
  })

  it('removes padding when noPadding is true', () => {
    render(<CardContent noPadding>Content</CardContent>)
    const content = screen.getByText('Content').parentElement
    expect(content).not.toHaveClass('p-6')
  })

  it('applies custom className', () => {
    render(<CardContent className="custom">Content</CardContent>)
    const content = screen.getByText('Content').parentElement
    expect(content).toHaveClass('custom')
  })
})

describe('CardFooter', () => {
  it('renders children', () => {
    render(<CardFooter>Footer content</CardFooter>)
    expect(screen.getByText('Footer content')).toBeInTheDocument()
  })

  it('aligns right by default', () => {
    render(<CardFooter>Footer</CardFooter>)
    const footer = screen.getByText('Footer').parentElement
    expect(footer).toHaveClass('justify-end')
  })

  it('aligns left when specified', () => {
    render(<CardFooter align="left">Footer</CardFooter>)
    const footer = screen.getByText('Footer').parentElement
    expect(footer).toHaveClass('justify-start')
  })

  it('aligns center when specified', () => {
    render(<CardFooter align="center">Footer</CardFooter>)
    const footer = screen.getByText('Footer').parentElement
    expect(footer).toHaveClass('justify-center')
  })

  it('aligns between when specified', () => {
    render(<CardFooter align="between">Footer</CardFooter>)
    const footer = screen.getByText('Footer').parentElement
    expect(footer).toHaveClass('justify-between')
  })

  it('applies custom className', () => {
    render(<CardFooter className="custom">Footer</CardFooter>)
    const footer = screen.getByText('Footer').parentElement
    expect(footer).toHaveClass('custom')
  })
})

// 组合测试
describe('Card composition', () => {
  it('renders full card with header, content, and footer', () => {
    render(
      <Card>
        <CardHeader title="Title" subtitle="Subtitle" />
        <CardContent>Body content</CardContent>
        <CardFooter>Footer</CardFooter>
      </Card>
    )

    expect(screen.getByText('Title')).toBeInTheDocument()
    expect(screen.getByText('Subtitle')).toBeInTheDocument()
    expect(screen.getByText('Body content')).toBeInTheDocument()
    expect(screen.getByText('Footer')).toBeInTheDocument()
  })
})
