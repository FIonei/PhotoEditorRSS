@file:Suppress("DEPRECATION")

package com.example.photoeditorrss

import android.app.Dialog
import android.app.ProgressDialog
import android.os.AsyncTask
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.photoeditorrss.Adapter.FeedAdapter

import com.example.photoeditorrss.Common.HTTPDataHandler
import com.example.photoeditorrss.Model.Root
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.rssdialog.*


class MainActivity : AppCompatActivity() {

    var dialog: Dialog? = null
    var RSS_link = "https://lenta.ru/rss/articles"
    private val RSS_to_Json_API = "https://api.rss2json.com/v1/api.json?rss_url="
    var res: String? = null
    var current_effect: String = "normal"
    var downloaded = false

    //create main activity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)

        toolbar.title = "Photo editor RSS"
        val linearLayoutManager = LinearLayoutManager(baseContext, RecyclerView.VERTICAL, false)
        recyclerView.layoutManager = linearLayoutManager

        loadRSS(current_effect)
    }

    //download RSS
    private fun loadRSS(effect: String) {
        val loadRSSAsync = object:AsyncTask<String, String, String>(){
            var mDialog = ProgressDialog(this@MainActivity)

            override fun onPreExecute() {
                mDialog.setMessage("Загрузка...")
                mDialog.show()
            }

            override fun onPostExecute(result: String?) {
                mDialog.dismiss()
                res = result
                val root: Root = Gson().fromJson<Root>(res, Root::class.java!!)
                toolbar.title = root.feed.title
                if ((toolbar.title == "")||(toolbar.title == null)) toolbar.title = "Новости"
                addEffect(effect)
                downloaded = true
            }

            override fun doInBackground(vararg params: String): String? {
                val result: String?
                val http = HTTPDataHandler()
                result = http.getHTTPDataHandler(params[0])
                return result
            }
        }
        val url_get_data = StringBuilder(RSS_to_Json_API)
        url_get_data.append(RSS_link)
        loadRSSAsync.execute(url_get_data.toString())
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    //part to download items with FeedAdapter
    fun addEffect(str: String){
        current_effect = str
        val root: Root = Gson().fromJson<Root>(res, Root::class.java!!)
        val adapter = FeedAdapter(root, baseContext, str)
        recyclerView.adapter = adapter
        adapter.notifyDataSetChanged()
    }

    //toolbar's features
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.menu_refresh) loadRSS("normal")
        else if (item.itemId == R.id.menu_search)
            showDialog()
        return true
    }

    //buttons for fast open some rss
    fun Habr(view: View){
        RSS_link = "https://habr.com/ru/rss/hubs/all/"
        loadRSS(current_effect)
    }
    fun Lenta(view: View){
        RSS_link = "https://lenta.ru/rss/articles"
        loadRSS(current_effect)
    }
    fun NYT(view:View){
        RSS_link = "https://rss.nytimes.com/services/xml/rss/nyt/HomePage.xml"
        loadRSS(current_effect)
    }

    //dialog to take user's rss
    fun showDialog() {
        dialog = Dialog(this@MainActivity)
        dialog!!.setContentView(R.layout.rssdialog)
        dialog!!.show()
    }
    fun getRss(view: View){
        RSS_link = dialog?.rss_link?.text.toString()
        if (RSS_link != "") {
            dialog?.dismiss()
            loadRSS(current_effect)
        }
        else dialog?.dismiss()
    }

    //functions to add filters
    fun normal(view: View){
        if ((current_effect!= "normal")||(!downloaded)) addEffect("normal")
    }

    fun sepia(view: View){
        if (current_effect!= "sepia") addEffect("sepia")
    }
    fun inversion(view: View){
        if (current_effect!= "inversion") addEffect("inversion")
    }
    fun sketch(view: View){
        if (current_effect!= "sketch") addEffect("sketch")
    }

}
