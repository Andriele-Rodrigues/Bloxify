package com.example.brickbreaker.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.brickbreaker.data.TeamMember

/**
 * Screen showcasing all 5 Bloxify project team members, their assigned roles, and architectural contributions.
 *
 * @param onBack Callback triggered when the user taps the "VOLTAR AO MENU" button.
 * @param members The list of team members to display, defaulting to [TeamMember.MEMBERS].
 */
@Composable
fun MembersScreen(
    onBack: () -> Unit,
    members: List<TeamMember> = TeamMember.MEMBERS
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .systemBarsPadding()
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Screen Header
        Text(
            text = "EQUIPE BLOXIFY",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            letterSpacing = 1.5.sp,
            textAlign = TextAlign.Center
        )

        Text(
            text = "Desenvolvedores do Projeto Android Brick Breaker",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
        )

        // Member Cards List
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 12.dp)
        ) {
            itemsIndexed(
                items = members,
                key = { _, member -> member.id }
            ) { index, member ->
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn(animationSpec = tween(300, delayMillis = index * 60)) +
                            slideInVertically(
                                initialOffsetY = { it / 4 },
                                animationSpec = tween(300, delayMillis = index * 60)
                            )
                ) {
                    MemberCard(member = member)
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Back to Menu Action Button
        Button(
            onClick = onBack,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            ),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 3.dp)
        ) {
            CanvasBackArrowIcon(
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "VOLTAR AO MENU",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

/**
 * Elevated card representing an individual team member with initials avatar, name, role badge, and responsibilities.
 */
@Composable
private fun MemberCard(
    member: TeamMember,
    modifier: Modifier = Modifier
) {
    val accentColor = Color(member.avatarColor)

    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .shadow(elevation = 2.dp, shape = RoundedCornerShape(16.dp))
            .border(
                BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                RoundedCornerShape(16.dp)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Circular Avatar with Initials
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            listOf(
                                accentColor.copy(alpha = 0.85f),
                                accentColor
                            )
                        )
                    )
                    .border(
                        BorderStroke(1.5.dp, Color.White.copy(alpha = 0.6f)),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = member.initials,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Member Info & Responsibilities
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = member.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f, fill = false)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    // Role Badge / Chip
                    Surface(
                        color = accentColor.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, accentColor.copy(alpha = 0.4f))
                    ) {
                        Text(
                            text = member.memberNumber,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = accentColor,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = member.role,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary
                )

                if (member.detailedResponsibilities.isNotEmpty() && member.detailedResponsibilities != member.role) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = member.detailedResponsibilities,
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

/**
 * Clean Canvas back arrow icon.
 */
@Composable
private fun CanvasBackArrowIcon(
    color: Color,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val strokeWidth = 2.5.dp.toPx()
        val centerY = size.height / 2f

        // Horizontal line
        drawLine(
            color = color,
            start = Offset(size.width * 0.15f, centerY),
            end = Offset(size.width * 0.85f, centerY),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
        // Top branch
        drawLine(
            color = color,
            start = Offset(size.width * 0.15f, centerY),
            end = Offset(size.width * 0.45f, centerY - size.height * 0.30f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
        // Bottom branch
        drawLine(
            color = color,
            start = Offset(size.width * 0.15f, centerY),
            end = Offset(size.width * 0.45f, centerY + size.height * 0.30f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
    }
}
