import { useState, useEffect } from 'react'
import Sidebar from '../components/Sidebar'
import api from '../api/axiosConfig'

const StatCard = ({ title, value, icon, color }) => (
  <div className="bg-white rounded-xl p-6 shadow-sm border border-gray-100">
    <div className="flex items-center justify-between">
      <div>
        <p className="text-sm text-gray-500">{title}</p>
        <p className={`text-2xl font-bold mt-1 ${color}`}>{value}</p>
      </div>
      <span className="text-3xl">{icon}</span>
    </div>
  </div>
)

const Dashboard = () => {
  const [stats, setStats] = useState({
    totalLedgers: 0,
    totalStockItems: 0,
    lowStockItems: 0,
    customerLedgers: 0,
    supplierLedgers: 0,
  })
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    const fetchStats = async () => {
      try {
        const [ledgersRes, stockRes, lowStockRes] = await Promise.all([
          api.get('/ledgers'),
          api.get('/stock-items'),
          api.get('/stock-items/low-stock?threshold=10'),
        ])

        const ledgers = ledgersRes.data
        setStats({
          totalLedgers: ledgers.length,
          totalStockItems: stockRes.data.length,
          lowStockItems: lowStockRes.data.length,
          customerLedgers: ledgers.filter(l => l.type === 'CUSTOMER').length,
          supplierLedgers: ledgers.filter(l => l.type === 'SUPPLIER').length,
        })
      } catch (err) {
        console.error('Failed to fetch stats', err)
      } finally {
        setLoading(false)
      }
    }

    fetchStats()
  }, [])

  return (
    <div className="flex min-h-screen bg-gray-50">
      <Sidebar />
      <main className="flex-1 p-8">
        <div className="mb-8">
          <h1 className="text-2xl font-bold text-gray-800">Dashboard</h1>
          <p className="text-gray-500 text-sm mt-1">Welcome to SmartERP</p>
        </div>

        {loading ? (
          <div className="text-gray-400 text-sm">Loading...</div>
        ) : (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            <StatCard title="Total Ledgers" value={stats.totalLedgers} icon="📒" color="text-blue-600" />
            <StatCard title="Customer Ledgers" value={stats.customerLedgers} icon="👤" color="text-green-600" />
            <StatCard title="Supplier Ledgers" value={stats.supplierLedgers} icon="🏭" color="text-purple-600" />
            <StatCard title="Stock Items" value={stats.totalStockItems} icon="📦" color="text-orange-600" />
            <StatCard title="Low Stock Alerts" value={stats.lowStockItems} icon="⚠️" color="text-red-600" />
          </div>
        )}
      </main>
    </div>
  )
}

export default Dashboard