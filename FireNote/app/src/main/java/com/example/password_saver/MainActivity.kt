package com.example.password_saver

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import com.example.password_saver.Auth.Login
import com.example.password_saver.Dados.Senha
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.google.firebase.database.FirebaseDatabase

class  MainActivity : AppCompatActivity() {

    private var _interstitialAd: InterstitialAd? = null
    private var _bannerAd: AdView? = null
    private var _rewardAd: RewardedAd? = null




    override fun onCreate(savedInstanceState: Bundle?) {
        val getUserpath = intent.getStringExtra("User")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val btn = findViewById<Button>(R.id.btn_salvar)
        val btn_result = findViewById<Button>(R.id.btn_senhas)
        val btn_sair = findViewById<TextView>(R.id.txt_sair)
        val btn_alterar = findViewById<Button>(R.id.txt_alterar)
        Log.d("value","$getUserpath")

        val nome = findViewById<EditText>(R.id.nome).text
        val senha = findViewById<EditText>(R.id.senha)


        btn.isEnabled = false

        senha.doAfterTextChanged {
            if(nome.isNotEmpty() and senha.text.isNotEmpty()){
                btn.isEnabled = true
            }
        }


        btn.setOnClickListener {
            saveAll()

        }
        btn_result.setOnClickListener {
            val intent = Intent(this,senhas::class.java)
            intent.putExtra("UserS","${getUserpath}")
            startActivity(intent)
        }
        btn_sair.setOnClickListener{
            val intent = Intent(this,TelaInicial::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
            finish()
        }
        btn_alterar.setOnClickListener{
            val intent = Intent(this,MudarDados::class.java)
            intent.putExtra("UserS","${getUserpath}")
            startActivity(intent)
        }

        MobileAds.initialize(this)
        loadInterstitial()
        loadRewardAd()
        loadBannerAd()
    }

    private fun loadRewardAd() {
        var adRequest = AdRequest.Builder().build()

        RewardedAd.load(this, "ca-app-pub-3940256099942544/5224354917", adRequest, object : RewardedAdLoadCallback() {
            override fun onAdFailedToLoad(p0: LoadAdError) {
                _rewardAd = null
            }

            override fun onAdLoaded(p0: RewardedAd) {
                _rewardAd = p0
            }
        })
    }

    private fun loadInterstitial() {
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(this, "ca-app-pub-3940256099942544/1033173712", adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                _interstitialAd = null;
            }

            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                _interstitialAd = interstitialAd
            }
        })
    }

    private fun loadBannerAd() {
        _bannerAd = findViewById(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        _bannerAd?.loadAd(adRequest)
    }

    fun showInterstitial(view: View) {
        _interstitialAd?.show(this)
    }

    fun showReward(view: View) {
        _rewardAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                _rewardAd = null
                loadRewardAd()
            }
        }

        _rewardAd?.show(this, OnUserEarnedRewardListener { rewardItem ->
            var rewardAmount = rewardItem.amount
            var rewardType = rewardItem.type
            println("$rewardAmount $rewardType Awarded")
        })
    }

    private fun saveAll() {

        val getUserpath = intent.getStringExtra("User")
        val nome = findViewById<EditText>(R.id.nome).text.toString().trim()
        val senha = findViewById<EditText>(R.id.senha).text.toString().trim()

        val ref = FirebaseDatabase.getInstance().getReference("${getUserpath}")

//        val db = FirebaseFirestore.getInstance()
//        db.collection("senhas")
//        val senhaid = ref.push().key

        if(senha.isEmpty() or nome.isEmpty()){
            Toast.makeText(this,"Preencha todos os campos!!",Toast.LENGTH_SHORT).show()
        }else{
            val senhaDB = Senha(nome,senha)



            ref.child(nome).setValue(senhaDB)

            Toast.makeText(this,"Itens Salvos",Toast.LENGTH_SHORT).show()

        }


    }


}