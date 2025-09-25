package com.pathtracker.utils

import android.content.Context
import android.os.Environment
import com.pathtracker.data.entities.*
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*

class KmlExporter {

    companion object {
        fun exportToKml(
            context: Context,
            path: PathEntity,
            pathPoints: List<PathPointEntity>,
            photos: List<PhotoEntity>,
            comments: List<CommentEntity>
        ): File? {
            try {
                val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                val fileName = "path_${path.name.replace(" ", "_")}_${System.currentTimeMillis()}.kml"
                val file = File(downloadsDir, fileName)

                val kmlContent = generateKmlContent(path, pathPoints, photos, comments)

                FileWriter(file).use { writer ->
                    writer.write(kmlContent)
                }

                return file
            } catch (e: Exception) {
                e.printStackTrace()
                return null
            }
        }

        private fun generateKmlContent(
            path: PathEntity,
            pathPoints: List<PathPointEntity>,
            photos: List<PhotoEntity>,
            comments: List<CommentEntity>
        ): String {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val startDate = dateFormat.format(Date(path.startTime))
            val endDate = if (path.endTime != null) dateFormat.format(Date(path.endTime)) else "In Progress"

            val kmlBuilder = StringBuilder()

            kmlBuilder.append("""
                <?xml version="1.0" encoding="UTF-8"?>
                <kml xmlns="http://www.opengis.net/kml/2.2">
                  <Document>
                    <name>${escapeXml(path.name)}</name>
                    <description><![CDATA[
                      <h3>Path Details</h3>
                      <p><strong>Description:</strong> ${escapeXml(path.description)}</p>
                      <p><strong>Start Time:</strong> $startDate</p>
                      <p><strong>End Time:</strong> $endDate</p>
                      <p><strong>Distance:</strong> ${String.format("%.2f", path.totalDistance)} km</p>
                      <p><strong>Average Speed:</strong> ${String.format("%.2f", path.averageSpeed)} km/h</p>
                      <p><strong>Rating:</strong> ${path.rating}/5.0</p>
                    ]]></description>

                    <!-- Style definitions -->
                    <Style id="pathStyle">
                      <LineStyle>
                        <color>ff0000ff</color>
                        <width>4</width>
                      </LineStyle>
                    </Style>

                    <Style id="photoStyle">
                      <IconStyle>
                        <Icon>
                          <href>http://maps.google.com/mapfiles/kml/shapes/camera.png</href>
                        </Icon>
                        <scale>1.2</scale>
                      </IconStyle>
                    </Style>

                    <Style id="commentStyle">
                      <IconStyle>
                        <Icon>
                          <href>http://maps.google.com/mapfiles/kml/shapes/info-i.png</href>
                        </Icon>
                        <scale>1.0</scale>
                      </IconStyle>
                    </Style>

                    <Style id="startStyle">
                      <IconStyle>
                        <Icon>
                          <href>http://maps.google.com/mapfiles/kml/shapes/flag.png</href>
                        </Icon>
                        <scale>1.3</scale>
                      </IconStyle>
                    </Style>

                    <Style id="endStyle">
                      <IconStyle>
                        <Icon>
                          <href>http://maps.google.com/mapfiles/kml/shapes/checkered_flag.png</href>
                        </Icon>
                        <scale>1.3</scale>
                      </IconStyle>
                    </Style>

            """.trimIndent())

            // Add path line
            if (pathPoints.isNotEmpty()) {
                kmlBuilder.append("""
                    <!-- Path Line -->
                    <Placemark>
                      <name>Walking Path</name>
                      <description>GPS tracked walking route</description>
                      <styleUrl>#pathStyle</styleUrl>
                      <LineString>
                        <tessellate>1</tessellate>
                        <coordinates>
                """.trimIndent())

                pathPoints.forEach { point ->
                    kmlBuilder.append("${point.longitude},${point.latitude},${point.altitude}\n")
                }

                kmlBuilder.append("""
                        </coordinates>
                      </LineString>
                    </Placemark>

                """.trimIndent())

                // Add start and end markers
                val startPoint = pathPoints.first()
                val endPoint = pathPoints.last()

                kmlBuilder.append("""
                    <!-- Start Point -->
                    <Placemark>
                      <name>Start</name>
                      <description>Path starting point - ${dateFormat.format(Date(startPoint.timestamp))}</description>
                      <styleUrl>#startStyle</styleUrl>
                      <Point>
                        <coordinates>${startPoint.longitude},${startPoint.latitude},${startPoint.altitude}</coordinates>
                      </Point>
                    </Placemark>

                    <!-- End Point -->
                    <Placemark>
                      <name>End</name>
                      <description>Path ending point - ${dateFormat.format(Date(endPoint.timestamp))}</description>
                      <styleUrl>#endStyle</styleUrl>
                      <Point>
                        <coordinates>${endPoint.longitude},${endPoint.latitude},${endPoint.altitude}</coordinates>
                      </Point>
                    </Placemark>

                """.trimIndent())
            }

            // Add photo points
            photos.forEach { photo ->
                val photoTime = dateFormat.format(Date(photo.timestamp))
                kmlBuilder.append("""
                    <!-- Photo Point -->
                    <Placemark>
                      <name>Photo</name>
                      <description><![CDATA[
                        <h3>Photo Location</h3>
                        <p><strong>Caption:</strong> ${escapeXml(photo.caption)}</p>
                        <p><strong>Time:</strong> $photoTime</p>
                        <p><strong>Location:</strong> ${String.format("%.6f", photo.latitude)}, ${String.format("%.6f", photo.longitude)}</p>
                      ]]></description>
                      <styleUrl>#photoStyle</styleUrl>
                      <Point>
                        <coordinates>${photo.longitude},${photo.latitude},0</coordinates>
                      </Point>
                    </Placemark>

                """.trimIndent())
            }

            // Add comment points
            comments.forEach { comment ->
                val commentTime = dateFormat.format(Date(comment.timestamp))
                if (comment.latitude != null && comment.longitude != null) {
                    kmlBuilder.append("""
                        <!-- Comment Point -->
                        <Placemark>
                          <name>Comment</name>
                          <description><![CDATA[
                            <h3>User Comment</h3>
                            <p>${escapeXml(comment.comment)}</p>
                            <p><strong>Time:</strong> $commentTime</p>
                            <p><strong>Location:</strong> ${String.format("%.6f", comment.latitude)}, ${String.format("%.6f", comment.longitude)}</p>
                          ]]></description>
                          <styleUrl>#commentStyle</styleUrl>
                          <Point>
                            <coordinates>${comment.longitude},${comment.latitude},0</coordinates>
                          </Point>
                        </Placemark>

                    """.trimIndent())
                }
            }

            kmlBuilder.append("""
                  </Document>
                </kml>
            """.trimIndent())

            return kmlBuilder.toString()
        }

        private fun escapeXml(text: String): String {
            return text.replace("&", "&amp;")
                      .replace("<", "&lt;")
                      .replace(">", "&gt;")
                      .replace("\"", "&quot;")
                      .replace("'", "&apos;")
        }
    }
}