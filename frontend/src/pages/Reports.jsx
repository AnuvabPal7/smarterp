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

const Reports = () => {
  const [report, setReport] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  useEffect(() => {
    const fetchReport = async () => {
      try {
        const res = await api.get('/reports')
        setReport(res.data)
      } catch (err) {
        setError('Failed to load report')
        console.error(err)
      } finally {
        setLoading(false)
      }
    }
    fetchReport()
  }, [])

  return (
    <div className="flex min-h-screen bg-gray-50">
      <Sidebar />
      <main className="flex-1 p-8">
        <div className="mb-8">
          <h1 className="text-2xl font-bold text-gray-800">Reports</h1>
          <p className="text-gray-500 text-sm mt-1">Business summary and AI insights</p>
        </div>

        {loading ? (
          <div className="text-gray-400 text-sm">Generating report...</div>
        ) : error ? (
          <div className="text-red-500 text-sm">{error}</div>
        ) : (
          <>
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6 mb-8">
              <StatCard title="Total Sales" value={`Rs. ${report.totalSalesAmount.toLocaleString()}`} icon="💰" color="text-green-600" />
              <StatCard title="Total Purchases" value={`Rs. ${report.totalPurchaseAmount.toLocaleString()}`} icon="🛒" color="text-purple-600" />
              <StatCard title="Net Profit/Loss" value={`Rs. ${report.totalProfit.toLocaleString()}`} icon="📈" color={report.totalProfit >= 0 ? 'text-green-600' : 'text-red-600'} />
              <StatCard title="Total Invoices" value={report.totalInvoices} icon="🧾" color="text-blue-600" />
              <StatCard title="Sales Invoices" value={report.salesInvoiceCount} icon="📤" color="text-green-600" />
              <StatCard title="Purchase Invoices" value={report.purchaseInvoiceCount} icon="📥" color="text-purple-600" />
            </div>

            {/* AI Summary */}
            <div className="bg-white rounded-xl p-6 shadow-sm border border-gray-100 mb-6">
              <div className="flex items-center gap-2 mb-3">
                <span className="text-2xl">🤖</span>
                <h2 className="text-lg font-semibold text-gray-800">AI Business Insights</h2>
                <span className="text-xs bg-blue-100 text-blue-600 px-2 py-1 rounded-full">Powered by Groq</span>
              </div>
              <p className="text-gray-600 text-sm leading-relaxed">{report.aiSummary}</p>
            </div>

            {/* Low Stock */}
            {report.lowStockItems.length > 0 && (
              <div className="bg-white rounded-xl p-6 shadow-sm border border-gray-100">
                <h2 className="text-lg font-semibold text-gray-800 mb-4">⚠️ Low Stock Items</h2>
                <table className="w-full">
                  <thead className="bg-gray-50">
                    <tr>
                      <th className="text-left text-xs font-medium text-gray-500 px-4 py-3">Item</th>
                      <th className="text-left text-xs font-medium text-gray-500 px-4 py-3">SKU</th>
                      <th className="text-left text-xs font-medium text-gray-500 px-4 py-3">Quantity</th>
                      <th className="text-left text-xs font-medium text-gray-500 px-4 py-3">Unit</th>
                    </tr>
                  </thead>
                  <tbody>
                    {report.lowStockItems.map(item => (
                      <tr key={item.id} className="border-t border-gray-50">
                        <td className="px-4 py-3 text-sm text-gray-800">{item.name}</td>
                        <td className="px-4 py-3 text-sm text-gray-600">{item.sku}</td>
                        <td className="px-4 py-3 text-sm text-red-600 font-medium">{item.quantity}</td>
                        <td className="px-4 py-3 text-sm text-gray-600">{item.unit}</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )}
          </>
        )}
      </main>
    </div>
  )
}

export default Reports