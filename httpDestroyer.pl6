use HTTP::Client;
sub send() {
    my $client = HTTP::Client.new;
    my $response = $client.get('http://127.0.0.1:4000');
    if ($response.success) {
        say $response.content;
    }
}
say "running client " ~ @*ARGS[0];
while True {
send();
}
