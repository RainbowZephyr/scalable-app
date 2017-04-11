data = File.read!("go.gif")
encoded = :base64.encode(data)
IO.puts encoded
