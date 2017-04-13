data = File.read!("TSWBG.jpg")
encoded = :base64.encode(data)
IO.puts encoded
