package com.example.ui.screens.idx

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.security.AppCheckSecurityManager
import com.example.ui.components.CodeBlockView
import com.example.ui.components.GlassmorphicCard
import com.example.ui.components.StatusBadge
import com.example.ui.theme.DevAmber
import com.example.ui.theme.DevEmeraldLight
import com.example.ui.theme.DevRose

@Composable
fun FirebaseSection(
    onSaveSnippet: (String, String, String, String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var activeModule by remember { mutableStateOf("Firestore") }
    val appCheckStatus by AppCheckSecurityManager.status.collectAsStateWithLifecycle()

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Section Hero
        GlassmorphicCard(
            borderColor = DevAmber.copy(alpha = 0.4f),
            backgroundColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocalFireDepartment,
                            contentDescription = null,
                            tint = DevAmber,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Firebase Backend Suite",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    StatusBadge(text = "Serverless Cloud", color = DevAmber)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Seamless backend for Project IDX and web apps: Realtime Cloud Firestore, Google Auth via Credential Manager, Cloud Functions v2 triggers, and Firebase App Check with Play Integrity provider.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Live App Check Security Status Card
        GlassmorphicCard(
            borderColor = if (appCheckStatus.isInitialized) DevEmeraldLight.copy(alpha = 0.4f) else DevAmber.copy(alpha = 0.3f),
            backgroundColor = MaterialTheme.colorScheme.surface
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = "App Check Play Integrity",
                            tint = if (appCheckStatus.isInitialized) DevEmeraldLight else DevAmber,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Firebase App Check • Play Integrity",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    StatusBadge(
                        text = if (appCheckStatus.isInitialized) "Secured (Play Integrity)" else "Configuring",
                        color = if (appCheckStatus.isInitialized) DevEmeraldLight else DevAmber,
                        icon = if (appCheckStatus.isInitialized) Icons.Default.CheckCircle else Icons.Default.Security
                    )
                }

                Text(
                    text = "Sağlayıcı: ${appCheckStatus.providerName} | Durum: ${appCheckStatus.statusMessage}",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (appCheckStatus.lastTokenSnippet != null) {
                    Text(
                        text = "Doğrulanmış Token: ${appCheckStatus.lastTokenSnippet} (Otomatik Yenileme Etkin)",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        color = DevEmeraldLight
                    )
                }
            }
        }

        // Submodules Selector
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("Firestore", "Auth & Identity", "Functions v2", "App Check (Play Integrity)", "Security Rules").forEach { mod ->
                val isSelected = activeModule == mod
                Button(
                    onClick = { activeModule = mod },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSelected) DevAmber else MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = if (isSelected) Color(0xFF451A03) else MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(38.dp).testTag("firebase_tab_$mod")
                ) {
                    Text(mod, fontSize = 11.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                }
            }
        }

        // Architecture Card
        GlassmorphicCard(
            borderColor = Color(0xFF475569),
            backgroundColor = MaterialTheme.colorScheme.surface
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Firebase $activeModule Config & Schema",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    StatusBadge(text = "Active", color = DevEmeraldLight, icon = Icons.Default.CheckCircle)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = when (activeModule) {
                        "Firestore" -> "Multi-region NoSQL document store with live snapshot listeners and automatic offline synchronization."
                        "Auth & Identity" -> "Google Sign-In, OAuth2 providers, and JWT claims verified at edge and in Cloud Functions."
                        "Functions v2" -> "Cloud Run backed serverless functions responding to Firestore writes, storage uploads, and HTTP webhooks."
                        "App Check (Play Integrity)" -> "Google Play Integrity donanım kanıtlaması (attestation) ile backend API'lerini, Cloud Firestore'u ve Cloud Functions çağrılarını yetkisiz istemcilerden ve botlardan korur."
                        else -> "Declarative granular access control enforcing user boundaries and validating field schema types."
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        val snippet = when (activeModule) {
            "Firestore" -> """
// Querying active projects ordered by updated time
import { getFirestore, collection, query, where, orderBy, getDocs } from 'firebase/firestore';

const db = getFirestore();
const q = query(
  collection(db, 'workspaces', workspaceId, 'projects'),
  where('status', '==', 'active'),
  orderBy('updatedAt', 'desc')
);

const snapshot = await getDocs(q);
const projects = snapshot.docs.map(doc => ({ id: doc.id, ...doc.data() }));
""".trimIndent()

            "Auth & Identity" -> """
// Google Sign-In with Credential Manager integration
import { getAuth, GoogleAuthProvider, signInWithCredential } from 'firebase/auth';

const auth = getAuth();
async function authenticateUserWithIdToken(idToken: string) {
  const credential = GoogleAuthProvider.credential(idToken);
  const userCredential = await signInWithCredential(auth, credential);
  console.log('Signed in user UID:', userCredential.user.uid);
  return userCredential.user;
}
""".trimIndent()

            "Functions v2" -> """
// functions/src/index.ts (Cloud Functions v2)
import { onDocumentCreated } from 'firebase-functions/v2/firestore';
import { getFirestore } from 'firebase-admin/firestore';

export const onNewUserProject = onDocumentCreated('projects/{projectId}', async (event) => {
  const snap = event.data;
  if (!snap) return;

  const data = snap.data();
  console.log('Provisioning IDX workspace for project:', data.name);
  
  // Trigger automated build pipeline
  await getFirestore().collection('audit_logs').add({
    event: 'PROJECT_CREATED',
    projectId: event.params.projectId,
    timestamp: new Date()
  });
});
""".trimIndent()

            "App Check (Play Integrity)" -> """
// Android: Firebase App Check with Play Integrity Provider
package com.example

import android.app.Application
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory

class OmniDevApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)

        val firebaseAppCheck = FirebaseAppCheck.getInstance()
        // Play Integrity Provider enables hardware-backed attestation
        firebaseAppCheck.installAppCheckProviderFactory(
            PlayIntegrityAppCheckProviderFactory.getInstance()
        )
        firebaseAppCheck.setTokenAutoRefreshEnabled(true)
    }
}

// Custom API requests (OkHttp Interceptor)
class AppCheckInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val appCheck = FirebaseAppCheck.getInstance()
        val token = Tasks.await(appCheck.getAppCheckToken(false), 2, TimeUnit.SECONDS)?.token
        val request = if (!token.isNullOrBlank()) {
            chain.request().newBuilder()
                .header("X-Firebase-AppCheck", token)
                .build()
        } else {
            chain.request()
        }
        return chain.proceed(request)
    }
}
""".trimIndent()

            else -> """
// firestore.rules
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    
    // User profile isolation
    match /users/{userId} {
      allow read: if request.auth != null;
      allow write: if request.auth != null && request.auth.uid == userId;
    }
    
    // Workspace collaborator authorization
    match /workspaces/{workspaceId}/projects/{projectId} {
      allow read, write: if request.auth != null && 
        exists(/databases/$(database)/documents/workspaces/$(workspaceId)/members/$(request.auth.uid));
    }
  }
}
""".trimIndent()
        }

        CodeBlockView(
            code = snippet,
            language = if (activeModule == "Security Rules") "json" else "typescript",
            title = "Firebase $activeModule",
            onSaveSnippet = {
                onSaveSnippet(
                    "Firebase $activeModule",
                    if (activeModule == "Security Rules") "json" else "typescript",
                    "Firebase",
                    snippet,
                    "Production pattern for Firebase $activeModule"
                )
            }
        )
    }
}
