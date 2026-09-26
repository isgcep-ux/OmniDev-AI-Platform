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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Storage
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
import com.example.ui.components.CodeBlockView
import com.example.ui.components.GlassmorphicCard
import com.example.ui.components.StatusBadge
import com.example.ui.theme.DevEmeraldLight
import com.example.ui.theme.DevIndigoLight

@Composable
fun NextJsSection(
    onSaveSnippet: (String, String, String, String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedFeature by remember { mutableStateOf("Server Components") }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Section Hero
        GlassmorphicCard(
            borderColor = DevIndigoLight.copy(alpha = 0.4f),
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
                            imageVector = Icons.Default.Language,
                            contentDescription = null,
                            tint = DevIndigoLight,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Next.js Web Platform",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    StatusBadge(text = "App Router", color = DevIndigoLight)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Full-stack React framework in Project IDX with React Server Components (RSC), Zero-bundle streaming SSR, Server Actions, and Edge Middleware.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Architecture Switcher Tabs
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("Server Components", "Server Actions", "Middleware").forEach { feature ->
                val isSelected = selectedFeature == feature
                Button(
                    onClick = { selectedFeature = feature },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSelected) DevIndigoLight else MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = if (isSelected) Color(0xFF1E1B4B) else MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f).height(40.dp).testTag("nextjs_tab_$feature")
                ) {
                    Text(feature, fontSize = 11.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                }
            }
        }

        // Interactive Architecture Visualizer Card
        GlassmorphicCard(
            borderColor = Color(0xFF475569),
            backgroundColor = MaterialTheme.colorScheme.surface
        ) {
            Column {
                Text(
                    text = when (selectedFeature) {
                        "Server Components" -> "RSC vs Client Components Boundary"
                        "Server Actions" -> "Zero-API Mutation Pipeline"
                        else -> "Edge Middleware Execution Chain"
                    },
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF0F172A))
                            .border(1.dp, Color(0xFF334155), RoundedCornerShape(8.dp))
                            .padding(10.dp)
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Dns, contentDescription = null, tint = DevEmeraldLight, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Server Tier", fontSize = 11.sp, color = DevEmeraldLight, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = when (selectedFeature) {
                                    "Server Components" -> "• Direct DB Fetch\n• 0kb Client Bundle\n• Sensitive Keys Safe"
                                    "Server Actions" -> "• 'use server' handler\n• Automatic CSRF guard\n• revalidatePath() cache"
                                    else -> "• Edge runtime\n• Auth header inspection\n• Response rewrites"
                                },
                                fontSize = 11.sp,
                                color = Color(0xFFCBD5E1),
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF0F172A))
                            .border(1.dp, Color(0xFF334155), RoundedCornerShape(8.dp))
                            .padding(10.dp)
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Language, contentDescription = null, tint = DevIndigoLight, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Client Tier", fontSize = 11.sp, color = DevIndigoLight, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = when (selectedFeature) {
                                    "Server Components" -> "• 'use client' directive\n• React useState/hooks\n• DOM event listeners"
                                    "Server Actions" -> "• Form submission\n• useActionState hook\n• Optimistic UI update"
                                    else -> "• Seamless page transition\n• Cookie retention\n• No roundtrip lag"
                                },
                                fontSize = 11.sp,
                                color = Color(0xFFCBD5E1),
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }
        }

        // Code Snippet based on selection
        val snippetCode = when (selectedFeature) {
            "Server Components" -> """
// app/dashboard/page.tsx (Server Component by default)
import { db } from '@/lib/db';
import { ProjectCard } from '@/components/ProjectCard'; // Client Component

export default async function DashboardPage() {
  // Direct server-side data query - Zero client JS payload!
  const projects = await db.project.findMany({
    orderBy: { createdAt: 'desc' },
  });

  return (
    <main className="p-8 max-w-6xl mx-auto">
      <h1 className="text-3xl font-bold tracking-tight">Project IDX Workspace</h1>
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mt-6">
        {projects.map((item) => (
          <ProjectCard key={item.id} project={item} />
        ))}
      </div>
    </main>
  );
}
""".trimIndent()

            "Server Actions" -> """
// app/actions/deployments.ts
'use server'

import { revalidatePath } from 'next/cache';
import { db } from '@/lib/db';

export async function triggerCloudDeployment(formData: FormData) {
  const projectId = formData.get('projectId') as string;
  const branch = formData.get('branch') as string;

  const deployment = await db.deployment.create({
    data: {
      projectId,
      branch,
      status: 'QUEUED',
      createdAt: new Date(),
    },
  });

  // Automatically refresh server-side cache for all viewers
  revalidatePath('/dashboard');
  return { success: true, deploymentId: deployment.id };
}
""".trimIndent()

            else -> """
// middleware.ts
import { NextResponse } from 'next/server';
import type { NextRequest } from 'next/server';

export function middleware(request: NextRequest) {
  const token = request.cookies.get('session_token')?.value;

  // Protect /admin and /workspace routes at Edge
  if (!token && request.nextUrl.pathname.startsWith('/workspace')) {
    return NextResponse.redirect(new URL('/login', request.url));
  }

  const response = NextResponse.next();
  response.headers.set('x-idx-powered-by', 'Google-Project-IDX');
  return response;
}

export const config = {
  matcher: ['/workspace/:path*', '/api/secure/:path*'],
};
""".trimIndent()
        }

        CodeBlockView(
            code = snippetCode,
            language = "typescript",
            title = "Next.js 15 $selectedFeature",
            onSaveSnippet = {
                onSaveSnippet(
                    "Next.js 15 $selectedFeature",
                    "typescript",
                    "Next.js",
                    snippetCode,
                    "Next.js App Router pattern for $selectedFeature"
                )
            }
        )
    }
}
