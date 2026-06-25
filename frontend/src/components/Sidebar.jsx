import { Link, useLocation, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'

const Sidebar = () => {
  const { user, logout } = useAuth()
  const location = useLocation()
  const navigate = useNavigate()

  const handleLogout = () => {
    logout()
    navigate('/login')
  }

  const navItems = [
    { path: '/', label: 'Dashboard', icon: '📊' },
    { path: '/ledgers', label: 'Ledgers', icon: '📒' },
    { path: '/stock-items', label: 'Stock Items', icon: '📦' },
  ]

  return (
    <div className="w-64 bg-gray-900 text-white min-h-screen flex flex-col">
      <div className="p-6 border-b border-gray-700">
        <h1 className="text-xl font-bold text-blue-400">SmartERP</h1>
        <p className="text-xs text-gray-400 mt-1">{user?.name || 'User'}</p>
      </div>

      <nav className="flex-1 p-4">
        {navItems.map((item) => (
          <Link
            key={item.path}
            to={item.path}
            className={`flex items-center gap-3 px-4 py-3 rounded-lg mb-1 transition-colors ${
              location.pathname === item.path
                ? 'bg-blue-600 text-white'
                : 'text-gray-300 hover:bg-gray-800'
            }`}
          >
            <span>{item.icon}</span>
            <span className="text-sm font-medium">{item.label}</span>
          </Link>
        ))}
      </nav>

      <div className="p-4 border-t border-gray-700">
        <button
          onClick={handleLogout}
          className="w-full px-4 py-2 text-sm text-red-400 hover:bg-gray-800 rounded-lg transition-colors text-left"
        >
          🚪 Logout
        </button>
      </div>
    </div>
  )
}

export default Sidebar