import { useState, useEffect } from 'react'
import Sidebar from '../components/Sidebar'
import api from '../api/axiosConfig'

const Vouchers = () => {
  const [invoices, setInvoices] = useState([])
  const [ledgers, setLedgers] = useState([])
  const [stockItems, setStockItems] = useState([])
  const [loading, setLoading] = useState(true)
  const [showForm, setShowForm] = useState(false)
  const [error, setError] = useState('')
  const [form, setForm] = useState({
    ledgerId: '',
    type: 'PURCHASE',
    items: [{ stockItemId: '', quantity: 1, rate: 0 }]
  })

  const fetchData = async () => {
    try {
      const [invoicesRes, ledgersRes, stockRes] = await Promise.all([
        api.get('/invoices'),
        api.get('/ledgers'),
        api.get('/stock-items'),
      ])
      setInvoices(invoicesRes.data)
      setLedgers(ledgersRes.data)
      setStockItems(stockRes.data)
    } catch (err) {
      console.error('Failed to fetch data', err)
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => { fetchData() }, [])

  const addItem = () => {
    setForm({ ...form, items: [...form.items, { stockItemId: '', quantity: 1, rate: 0 }] })
  }

  const removeItem = (index) => {
    setForm({ ...form, items: form.items.filter((_, i) => i !== index) })
  }

  const updateItem = (index, field, value) => {
    const updated = [...form.items]
    updated[index][field] = value
    setForm({ ...form, items: updated })
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError('')
    try {
      await api.post('/invoices', {
        ...form,
        ledgerId: parseInt(form.ledgerId),
        items: form.items.map(item => ({
          stockItemId: parseInt(item.stockItemId),
          quantity: parseInt(item.quantity),
          rate: parseFloat(item.rate)
        }))
      })
      setForm({ ledgerId: '', type: 'PURCHASE', items: [{ stockItemId: '', quantity: 1, rate: 0 }] })
      setShowForm(false)
      fetchData()
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to create voucher')
    }
  }

  const filteredLedgers = ledgers.filter(l =>
    form.type === 'PURCHASE' ? l.type === 'SUPPLIER' : l.type === 'CUSTOMER'
  )

  const typeColors = {
    SALES: 'bg-green-100 text-green-700',
    PURCHASE: 'bg-purple-100 text-purple-700',
  }

  const statusColors = {
    PENDING: 'bg-yellow-100 text-yellow-700',
    PAID: 'bg-green-100 text-green-700',
    CANCELLED: 'bg-red-100 text-red-700',
  }

  return (
    <div className="flex min-h-screen bg-gray-50">
      <Sidebar />
      <main className="flex-1 p-8">
        <div className="flex items-center justify-between mb-8">
          <div>
            <h1 className="text-2xl font-bold text-gray-800">Vouchers</h1>
            <p className="text-gray-500 text-sm mt-1">Manage Sales & Purchase Vouchers</p>
          </div>
          <button
            onClick={() => setShowForm(!showForm)}
            className="bg-blue-600 text-white px-4 py-2 rounded-lg text-sm font-medium hover:bg-blue-700 transition-colors"
          >
            + New Voucher
          </button>
        </div>

        {showForm && (
          <div className="bg-white rounded-xl p-6 shadow-sm border border-gray-100 mb-6">
            <h2 className="text-lg font-semibold text-gray-800 mb-4">New Voucher</h2>
            {error && <div className="bg-red-50 text-red-600 text-sm px-4 py-3 rounded-lg mb-4">{error}</div>}
            <form onSubmit={handleSubmit} className="space-y-4">
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">Type</label>
                  <select
                    value={form.type}
                    onChange={(e) => setForm({ ...form, type: e.target.value, ledgerId: '' })}
                    className="w-full border border-gray-300 rounded-lg px-4 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
                  >
                    <option value="PURCHASE">Purchase</option>
                    <option value="SALES">Sales</option>
                  </select>
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    {form.type === 'PURCHASE' ? 'Supplier' : 'Customer'}
                  </label>
                  <select
                    value={form.ledgerId}
                    onChange={(e) => setForm({ ...form, ledgerId: e.target.value })}
                    className="w-full border border-gray-300 rounded-lg px-4 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
                    required
                  >
                    <option value="">Select {form.type === 'PURCHASE' ? 'Supplier' : 'Customer'}</option>
                    {filteredLedgers.map(l => (
                      <option key={l.id} value={l.id}>{l.name}</option>
                    ))}
                  </select>
                </div>
              </div>

              <div>
                <div className="flex items-center justify-between mb-2">
                  <label className="block text-sm font-medium text-gray-700">Items</label>
                  <button type="button" onClick={addItem} className="text-blue-600 text-xs hover:underline">+ Add Item</button>
                </div>
                {form.items.map((item, index) => (
                  <div key={index} className="grid grid-cols-1 md:grid-cols-4 gap-3 mb-3">
                    <select
                      value={item.stockItemId}
                      onChange={(e) => updateItem(index, 'stockItemId', e.target.value)}
                      className="border border-gray-300 rounded-lg px-4 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
                      required
                    >
                      <option value="">Select Item</option>
                      {stockItems.map(s => (
                        <option key={s.id} value={s.id}>{s.name} (Stock: {s.quantity})</option>
                      ))}
                    </select>
                    <input
                      type="number"
                      placeholder="Quantity"
                      value={item.quantity}
                      onChange={(e) => updateItem(index, 'quantity', e.target.value)}
                      className="border border-gray-300 rounded-lg px-4 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
                      required
                    />
                    <input
                      type="number"
                      placeholder="Rate"
                      value={item.rate}
                      onChange={(e) => updateItem(index, 'rate', e.target.value)}
                      className="border border-gray-300 rounded-lg px-4 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
                      required
                    />
                    {form.items.length > 1 && (
                      <button type="button" onClick={() => removeItem(index)} className="text-red-500 text-xs hover:text-red-700">Remove</button>
                    )}
                  </div>
                ))}
              </div>

              <div className="flex gap-3">
                <button type="submit" className="bg-blue-600 text-white px-6 py-2 rounded-lg text-sm font-medium hover:bg-blue-700 transition-colors">
                  Create Voucher
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
                  <th className="text-left text-xs font-medium text-gray-500 px-6 py-4">Invoice No.</th>
                  <th className="text-left text-xs font-medium text-gray-500 px-6 py-4">Type</th>
                  <th className="text-left text-xs font-medium text-gray-500 px-6 py-4">Party</th>
                  <th className="text-left text-xs font-medium text-gray-500 px-6 py-4">Date</th>
                  <th className="text-left text-xs font-medium text-gray-500 px-6 py-4">Amount</th>
                  <th className="text-left text-xs font-medium text-gray-500 px-6 py-4">Status</th>
                </tr>
              </thead>
              <tbody>
                {invoices.length === 0 ? (
                  <tr><td colSpan="6" className="text-center text-gray-400 text-sm py-8">No vouchers found</td></tr>
                ) : (
                  invoices.map((inv) => (
                    <tr key={inv.id} className="border-b border-gray-50 hover:bg-gray-50">
                      <td className="px-6 py-4 text-sm text-gray-800 font-medium">{inv.invoiceNumber}</td>
                      <td className="px-6 py-4">
                        <span className={`text-xs px-2 py-1 rounded-full font-medium ${typeColors[inv.type]}`}>
                          {inv.type}
                        </span>
                      </td>
                      <td className="px-6 py-4 text-sm text-gray-600">{inv.ledgerName}</td>
                      <td className="px-6 py-4 text-sm text-gray-600">{inv.date}</td>
                      <td className="px-6 py-4 text-sm text-gray-800 font-medium">₹{inv.totalAmount.toLocaleString()}</td>
                      <td className="px-6 py-4">
                        <span className={`text-xs px-2 py-1 rounded-full font-medium ${statusColors[inv.status]}`}>
                          {inv.status}
                        </span>
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

export default Vouchers