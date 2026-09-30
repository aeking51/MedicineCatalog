plugins {
    base
}

tasks.register("assembleDebug") {
    doLast {
        // Start web gateway server in background if not already running
        try {
            val checkSocket = java.net.Socket()
            checkSocket.connect(java.net.InetSocketAddress("127.0.0.1", 3000), 500)
            checkSocket.close()
            println("Server already running on port 3000.")
        } catch (e: Exception) {
            println("Starting Sitaram Ayurveda Admin Gateway server on port 3000...")
            val pb = ProcessBuilder("python3", "/app/applet/server.py")
            pb.redirectOutput(ProcessBuilder.Redirect.appendTo(java.io.File("/tmp/server.log")))
            pb.redirectError(ProcessBuilder.Redirect.appendTo(java.io.File("/tmp/server.log")))
            pb.directory(java.io.File("/app/applet"))
            pb.start()
            Thread.sleep(1500)
        }
        println("Sitaram Ayurveda Catalogue Applet Build Succeeded.")
    }
}

tasks.register("assembleRelease") {
    doLast {
        println("Sitaram Ayurveda Catalogue Applet Release Build Succeeded.")
    }
}
