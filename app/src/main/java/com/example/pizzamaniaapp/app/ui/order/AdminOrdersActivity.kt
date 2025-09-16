package com.example.pizzamaniaapp.app.ui.order

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.example.pizzamaniaapp.R
import com.pizzamania.data.model.Order
import com.pizzamania.data.repo.AuthRepo
import kotlinx.coroutines.launch
import com.google.firebase.Timestamp



class AdminOrdersActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_orders)


        val recycler = findViewById<RecyclerView>(R.id.recycler)
        recycler.layoutManager = LinearLayoutManager(this)
        val adapter = SimpleOrdersAdapter()
        recycler.adapter = adapter

        lifecycleScope.launch {
            val (_, branchId) = AuthRepo(Firebase.auth, Firebase.firestore).currentRoleBranch()
            if (branchId == null) return@launch
            Firebase.firestore.collection("orders").whereEqualTo("branchId", branchId)
                .addSnapshotListener { qs, _ ->
                    val items = qs?.documents?.map { d ->
                        Order(
                            id = d.id,
                            userId = d.getString("userId") ?: "",
                            branchId = branchId,
                            items = emptyList(),
                            total = d.getDouble("total") ?: 0.0,
                            address = d.getString("address") ?: "",
                            phone = d.getString("phone") ?: "",
                            payment = d.getString("payment") ?: "COD",
                            status = d.getString("status") ?: "Pending",
                            createdAt = d.getTimestamp("createdAt") ?: Timestamp.now()
                        )
                    } ?: emptyList()
                    adapter.submit(items)
                    findViewById<TextView>(R.id.emptyState).visibility = if (items.isEmpty()) View.VISIBLE else View.GONE
                }
        }
    }
}

class SimpleOrdersAdapter : RecyclerView.Adapter<SimpleOrdersVH>() {
    private val data = mutableListOf<Order>()
    fun submit(list: List<Order>) { data.clear(); data.addAll(list); notifyDataSetChanged() }
    override fun onCreateViewHolder(p: android.view.ViewGroup, vType: Int) =
        SimpleOrdersVH(android.view.LayoutInflater.from(p.context).inflate(android.R.layout.simple_list_item_2, p, false))
    override fun getItemCount() = data.size
    override fun onBindViewHolder(h: SimpleOrdersVH, i: Int) {
        val o = data[i]
        h.t1.text = "Order #${o.id.take(6)} • Rs. ${"%.2f".format(o.total)}"
        h.t2.text = "${o.status} • ${o.address}"
    }
}
class SimpleOrdersVH(v: android.view.View): RecyclerView.ViewHolder(v) {
    val t1: TextView = v.findViewById(android.R.id.text1)
    val t2: TextView = v.findViewById(android.R.id.text2)
}