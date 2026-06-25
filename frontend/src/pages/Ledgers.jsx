import { useState, useEffect } from 'react'
import Sidebar from '../components/Sidebar'
import api from '../api/axiosConfig'

const Ledgers = () => {
  const [ledgers, setLedgers] = useState([])
  const [loading, setLoading] = useState(true)
  const [showForm, setShowForm] = useState(false)
  const [form, setForm] = useState({ name: '', type: 'CUSTOMER', openingBalance: 0 })
  const [error, setError] = useState('')

  const fetchLedgers = async () => {
    try {
      const res = await api.get('/ledgers')
      setLedgers(res.data)
    } catch (err) {
      console.error('Failed to fetch ledgers', err)
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => { fetchLedgers() }, [])

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError('')
    try {
      await api.post('/ledgers', form)
      setForm({ name: '', type: 'CUSTOMER', openingBalance: 0 })
      setShowForm(false)
      fetchLedgers()
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to create ledger')
    }
  }

  const handleDelete = async (id) => {
    if (!confirm('Delete this ledger?')) return
    try {
      await api.delete(`/ledgers/${id}`)
      fetchLedgers()
    } catch (err) {
      console.error('Failed to delete', err)
    }
  }

  const typeColors = {
    CUSTOMER: 'bg-green-100 text-green-700',
    SUPPLIER: 'bg-purple-100 text-purple-700',
    EXPENSE: 'bg-red-100 text-red-700',
    INCOME: 'bg-blue-100 text-blue-700',
  }

  return (
    <div className="flex min-h-screen bg-gray-50">
      <Sidebar />
      <main className="flex-1 p-8">
        <div className="flex items-center justify-between mb-8">
          <div>
            <h1 className="text-2xl font-bold text-gray-800">Ledgers</h1>
            <p className="text-gray-500 text-sm mt-1">Manage your accounting ledgers</p>
          </div>
          <button
            onClick={() => setShowForm(!showForm)}
            className="bg-blue-600 text-white px-4 py-2 rounded-lg text-sm font-medium hover:bg-blue-700 transition-colors"
          >
            + Add Ledger
          </button>
        </div>

        {showForm && (
          <div className="bg-white rounded-xl p-6 shadow-sm border border-gray-100 mb-6">
            <h2 className="text-lg font-semibold text-gray-800 mb-4">New Ledger</h2>
            {error && <div className="bg-red-50 text-red-600 text-sm px-4 py-3 rounded-lg mb-4">{error}</div>}
            <form onSubmit={handleSubmit} className="grid grid-cols-1 md:grid-cols-3 gap-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Name</label>
                <input
                  type="text"
                  value={form.name}
                  onChange={(e) => setForm({ ...form, name: e.target.value })}
                  className="w-full border border-gray-300 rounded-lg px-4 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
                  required
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Type</label>
                <select
                  value={form.type}
                  onChange={(e) => setForm({ ...form, type: e.target.value })}
                  className="w-full border border-gray-300 rounded-lg px-4 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
                >
                  <option value="CUSTOMER">Customer</option>
                  <option value="SUPPLIER">Supplier</option>
                  <option value="EXPENSE">Expense</option>
                  <option value="INCOME">Income</option>
                </select>
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Opening Balance</label>
                <input
                  type="number"
                  value={form.openingBalance}
                  onChange={(e) => setForm({ ...form, openingBalance: parseFloat(e.target.value) })}
                  className="w-full border border-gray-300 rounded-lg px-4 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
                />
              </div>
              <div className="md:col-span-3 flex gap-3">
                <button type="submit" className="bg-blue-600 text-white px-6 py-2 rounded-lg text-sm font-medium hover:bg-blue-700 transition-colors">
                  Create
                </button>
                <button type="button" onClick={() => setShowForm(false)} className="bg-gray-100 text-gray-700 px-6 py-2 rounded-lg text-sm font-medium hover:bg-gray-200 transition-colors">
                  Cancel
                </button>
              </div>
            </form>
          </div>
        )}

        {loading ? (
          <div className="text-gray-400 text-sm">Loading...</div>
        ) : (
          <div className="bg-white rounded-xl shadow-sm border border-gray-100 overflow-hidden">
            <table className="w-full">
              <thead className="bg-gray-50 border-b border-gray-100">
                <tr>
                  <th className="text-left text-xs font-medium text-gray-500 px-6 py-4">Name</th>
                  <th className="text-left text-xs font-medium text-gray-500 px-6 py-4">Type</th>
                  <th className="text-left text-xs font-medium text-gray-500 px-6 py-4">Opening Balance</th>
                  <th className="text-left text-xs font-medium text-gray-500 px-6 py-4">Current Balance</th>
                  <th className="text-left text-xs font-medium text-gray-500 px-6 py-4">Actions</th>
                </tr>
              </thead>
              <tbody>
                {ledgers.length === 0 ? (
                  <tr><td colSpan="5" className="text-center text-gray-400 text-sm py-8">No ledgers found</td></tr>
                ) : (
                  ledgers.map((ledger) => (
                    <tr key={ledger.id} className="border-b border-gray-50 hover:bg-gray-50">
                      <td className="px-6 py-4 text-sm text-gray-800 font-medium">{ledger.name}</td>
                      <td className="px-6 py-4">
                        <span className={`text-xs px-2 py-1 rounded-full font-medium ${typeColors[ledger.type]}`}>
                          {ledger.type}
                        </span>
                      </td>
                      <td className="px-6 py-4 text-sm text-gray-600">₹{ledger.openingBalance.toLocaleString()}</td>
                      <td className="px-6 py-4 text-sm text-gray-600">₹{ledger.currentBalance.toLocaleString()}</td>
                      <td className="px-6 py-4">
                        <button
                          onClick={() => handleDelete(ledger.id)}
                          className="text-red-500 hover:text-red-700 text-xs font-medium"
                        >
                          Delete
                        </button>
                      </td>
                    </tr>
                  ))
                )}
              </tbody>
            </table>
          </div>
        )}
      </main>
    </div>
  )
}

export default Ledgers