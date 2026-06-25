import { useState, useEffect } from 'react'
import Sidebar from '../components/Sidebar'
import api from '../api/axiosConfig'

const StockItems = () => {
  const [items, setItems] = useState([])
  const [loading, setLoading] = useState(true)
  const [showForm, setShowForm] = useState(false)
  const [form, setForm] = useState({
    name: '', sku: '', purchasePrice: 0,
    sellingPrice: 0, quantity: 0, unit: 'PCS'
  })
  const [error, setError] = useState('')

  const fetchItems = async () => {
    try {
      const res = await api.get('/stock-items')
      setItems(res.data)
    } catch (err) {
      console.error('Failed to fetch stock items', err)
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => { fetchItems() }, [])

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError('')
    try {
      await api.post('/stock-items', form)
      setForm({ name: '', sku: '', purchasePrice: 0, sellingPrice: 0, quantity: 0, unit: 'PCS' })
      setShowForm(false)
      fetchItems()
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to create stock item')
    }
  }

  const handleDelete = async (id) => {
    if (!confirm('Delete this item?')) return
    try {
      await api.delete(`/stock-items/${id}`)
      fetchItems()
    } catch (err) {
      console.error('Failed to delete', err)
    }
  }

  return (
    <div className="flex min-h-screen bg-gray-50">
      <Sidebar />
      <main className="flex-1 p-8">
        <div className="flex items-center justify-between mb-8">
          <div>
            <h1 className="text-2xl font-bold text-gray-800">Stock Items</h1>
            <p className="text-gray-500 text-sm mt-1">Manage your inventory</p>
          </div>
          <button
            onClick={() => setShowForm(!showForm)}
            className="bg-blue-600 text-white px-4 py-2 rounded-lg text-sm font-medium hover:bg-blue-700 transition-colors"
          >
            + Add Item
          </button>
        </div>

        {showForm && (
          <div className="bg-white rounded-xl p-6 shadow-sm border border-gray-100 mb-6">
            <h2 className="text-lg font-semibold text-gray-800 mb-4">New Stock Item</h2>
            {error && <div className="bg-red-50 text-red-600 text-sm px-4 py-3 rounded-lg mb-4">{error}</div>}
            <form onSubmit={handleSubmit} className="grid grid-cols-1 md:grid-cols-3 gap-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Name</label>
                <input type="text" value={form.name} onChange={(e) => setForm({ ...form, name: e.target.value })}
                  className="w-full border border-gray-300 rounded-lg px-4 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500" required />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">SKU</label>
                <input type="text" value={form.sku} onChange={(e) => setForm({ ...form, sku: e.target.value })}
                  className="w-full border border-gray-300 rounded-lg px-4 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500" required />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Unit</label>
                <select value={form.unit} onChange={(e) => setForm({ ...form, unit: e.target.value })}
                  className="w-full border border-gray-300 rounded-lg px-4 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500">
                  <option>PCS</option>
                  <option>BOX</option>
                  <option>KG</option>
                  <option>LTR</option>
                </select>
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Purchase Price</label>
                <input type="number" value={form.purchasePrice} onChange={(e) => setForm({ ...form, purchasePrice: parseFloat(e.target.value) })}
                  className="w-full border border-gray-300 rounded-lg px-4 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500" required />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Selling Price</label>
                <input type="number" value={form.sellingPrice} onChange={(e) => setForm({ ...form, sellingPrice: parseFloat(e.target.value) })}
                  className="w-full border border-gray-300 rounded-lg px-4 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500" required />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Quantity</label>
                <input type="number" value={form.quantity} onChange={(e) => setForm({ ...form, quantity: parseInt(e.target.value) })}
                  className="w-full border border-gray-300 rounded-lg px-4 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500" required />
              </div>
              <div className="md:col-span-3 flex gap-3">
                <button type="submit" className="bg-blue-600 text-white px-6 py-2 rounded-lg text-sm font-medium hover:bg-blue-700 transition-colors">Create</button>
                <button type="button" onClick={() => setShowForm(false)} className="bg-gray-100 text-gray-700 px-6 py-2 rounded-lg text-sm font-medium hover:bg-gray-200 transition-colors">Cancel</button>
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
                  <th className="text-left text-xs font-medium text-gray-500 px-6 py-4">SKU</th>
                  <th className="text-left text-xs font-medium text-gray-500 px-6 py-4">Purchase Price</th>
                  <th className="text-left text-xs font-medium text-gray-500 px-6 py-4">Selling Price</th>
                  <th className="text-left text-xs font-medium text-gray-500 px-6 py-4">Quantity</th>
                  <th className="text-left text-xs font-medium text-gray-500 px-6 py-4">Unit</th>
                  <th className="text-left text-xs font-medium text-gray-500 px-6 py-4">Actions</th>
                </tr>
              </thead>
              <tbody>
                {items.length === 0 ? (
                  <tr><td colSpan="7" className="text-center text-gray-400 text-sm py-8">No stock items found</td></tr>
                ) : (
                  items.map((item) => (
                    <tr key={item.id} className="border-b border-gray-50 hover:bg-gray-50">
                      <td className="px-6 py-4 text-sm text-gray-800 font-medium">{item.name}</td>
                      <td className="px-6 py-4 text-sm text-gray-600">{item.sku}</td>
                      <td className="px-6 py-4 text-sm text-gray-600">₹{item.purchasePrice.toLocaleString()}</td>
                      <td className="px-6 py-4 text-sm text-gray-600">₹{item.sellingPrice.toLocaleString()}</td>
                      <td className="px-6 py-4">
                        <span className={`text-sm font-medium ${item.quantity < 10 ? 'text-red-600' : 'text-gray-800'}`}>
                          {item.quantity}
                        </span>
                      </td>
                      <td className="px-6 py-4 text-sm text-gray-600">{item.unit}</td>
                      <td className="px-6 py-4">
                        <button onClick={() => handleDelete(item.id)} className="text-red-500 hover:text-red-700 text-xs font-medium">
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

export default StockItems