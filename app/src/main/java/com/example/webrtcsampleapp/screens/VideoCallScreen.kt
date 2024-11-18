package com.example.webrtcsampleapp.screens
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight

import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.firebase.firestore.FirebaseFirestore
import org.webrtc.DataChannel
import org.webrtc.DefaultVideoDecoderFactory
import org.webrtc.DefaultVideoEncoderFactory
import org.webrtc.EglBase
import org.webrtc.IceCandidate
import org.webrtc.MediaStream
import org.webrtc.PeerConnection
import org.webrtc.PeerConnection.IceServer
import org.webrtc.PeerConnectionFactory
import org.webrtc.RtpReceiver
import org.webrtc.SurfaceViewRenderer

import timber.log.Timber

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoCallScreen (roomId:String,onNavigateBack:() -> Unit) {

    // create room :
    // room present :
        //-- check capacity
        //-- more then 2 -> left to home

    val firestore = FirebaseFirestore.getInstance()
    val eglBase = EglBase.create()
    var peerConnectionFactory: PeerConnectionFactory? =  null;
    var peerConnector:PeerConnection? = null
    val context = LocalContext.current

    fun initializeWebRTC() {
        Timber.e("initializeWebRTC() :: ")

        PeerConnectionFactory.initialize(
            PeerConnectionFactory.InitializationOptions
                .builder(context)
                .setEnableInternalTracer(true)
                .createInitializationOptions()
        )
        val videoEncodedFactory = DefaultVideoEncoderFactory(eglBase.eglBaseContext,true,false)
        val videoDecoderactory = DefaultVideoDecoderFactory(eglBase.eglBaseContext)

        peerConnectionFactory = PeerConnectionFactory
            .builder()
            .setVideoEncoderFactory(videoEncodedFactory)
            .setVideoDecoderFactory(videoDecoderactory)
            .createPeerConnectionFactory()
    }

    fun createPeerConnection() {
        Timber.e("createPeerConnection() :: ")

        val iceServers = IceServer.builder(
            listOf(
                "stun:stun1.l.google.com:19302",
                "stun:stun2.l.google.com:19302"
            )
        )
        val rtcCong = PeerConnection.RTCConfiguration(listOf(iceServers.createIceServer()))
        peerConnector = peerConnectionFactory?.createPeerConnection(
            rtcCong,
            object : PeerConnection.Observer {
                override fun onSignalingChange(p0: PeerConnection.SignalingState?) {

                }


                override fun onConnectionChange(newState: PeerConnection.PeerConnectionState?) {
                    super.onConnectionChange(newState)
                    Timber.e("createPeerConnection() :: onConnectionChange $newState :: ")

                }
                override fun onIceConnectionChange(p0: PeerConnection.IceConnectionState?) {

                }

                override fun onIceConnectionReceivingChange(p0: Boolean) {

                }

                override fun onIceGatheringChange(p0: PeerConnection.IceGatheringState?) {

                }

                override fun onIceCandidate(p0: IceCandidate?) {

                }

                override fun onIceCandidatesRemoved(p0: Array<out IceCandidate>?) {

                }

                override fun onAddStream(p0: MediaStream?) {

                }

                override fun onRemoveStream(p0: MediaStream?) {

                }

                override fun onDataChannel(p0: DataChannel?) {

                }

                override fun onRenegotiationNeeded() {

                }

                override fun onAddTrack(p0: RtpReceiver?, p1: Array<out MediaStream>?) {

                }

            }
        )
    }

    fun checkRoomCapacityAndSetup(
        onProceed: () -> Unit
    ) {
        // get document ref
        val roomDocRef = firestore.collection("rooms").document(roomId)
        roomDocRef.get().addOnSuccessListener { document ->
            Timber.d("Firebase firestore success !")
            // check if doc existe or not (room existe or not)
            if (document != null && document.exists()) {
                // check the participant count is 2 or not :
                val participantCount = (document["participantCount"] as? Long)?.toInt() ?: 0
                // if yes :
                if (participantCount >= 2) {
                    // show error message
                    Toast.makeText(
                        context,
                        "Room is FULL. Cannot join at the moment !",
                        Toast.LENGTH_LONG
                    ).show()
                    onNavigateBack.invoke()
                } else {
                    // if not :
                    // update the document update participantCount prop to participantCount+1
                    roomDocRef.update("participantCount", participantCount + 1)
                }
            } else {
                // create room :
                roomDocRef.set(mapOf("participantCount" to 1))
            }
        }.addOnFailureListener {
            Timber.e("Firebase Failed to get Firestore DB")
            Toast.makeText(
                context,
                "Firebase Failed to get Firestore DB !",
                Toast.LENGTH_LONG
            ).show()
            onNavigateBack.invoke()
        }
    }

    LaunchedEffect(Unit){
        checkRoomCapacityAndSetup(onProceed = {
            initializeWebRTC()
            createPeerConnection()
        })
    }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary
                ),
                title = {
                    Text(text = "WebRTC Sample !", fontWeight = FontWeight.Bold)
                })
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues = innerPadding)
        ) {
            AndroidView(
                factory = { context ->
                    SurfaceViewRenderer(context)
                },
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
            )

            Spacer(modifier = Modifier.height(20.dp))

            AndroidView(
                factory = { context ->
                    SurfaceViewRenderer(context)
                },
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
            )
        }
    }
}




@Preview(showBackground = true)
@Composable
fun PreviewVideoCallScreen(){
    VideoCallScreen("12345",{})
}