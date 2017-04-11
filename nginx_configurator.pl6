sub load_nginx_conf($path) {
    my $lines = slurp $path, :r;
    # $lines = $lines.grep({$_.contains("#")});
    return $lines.split(/\n/).grep({!$_.contains("#")}).grep(/<-[^$]>/);
}

sub check_cmdline_args(@args) {
    # say @args.pairup;
    my @options = ['-h', '-p', '-d', '-j', '-c'];

    if (@args.elems == 1 && @args[0] eqv "-h") {
        say "Option        Description\n-p            Enter path for Nginx config file default path is  /etc/nginx/nginx.conf\n-j            Enter jvm path default path is /usr/lib/default/jre/lib/amd64/server/libjvm.so\n-c            Enter class path default path is classpath ~/.m2/repository/nginx-clojure/nginx-clojure/*: ./src/main/java\n-d            Generate file using defaults:\n                   jvm path: /usr/lib/default/jre/lib/amd64/server/libjvm.so\n                   nginx original config file: /etc/nginx/nginx.conf\n                   classpath ~/.m2/repository/nginx-clojure/nginx-clojure/*: ./src/main/java";
    } elsif (@args.elems == 1 && @args[0] eqv "-d") {
        generate_defaults;
    } elsif @args.elems % 2 == 0 {
        if !(@args.pairup.hash.keys (-) @options).keys > 0 {
            my %hash = @args.pairup.hash;

        } else {
            die "Unknown options: "~ @args.pairup.hash.keys (-) @options;
        }
    } else {
        die "Unknown options"
    }
}

sub generate(%hash) {

}

sub generate_defaults() {
    my @oldfile = load_nginx_conf("/etc/nginx/nginx.conf");
    my @newfile;
    my $cwd;
    if($*KERNEL.name == "win32") {
        $cwd = $*CWD.dirname ~~ m/C:\/\/Users
    }

    my $j= 0;
    for @oldfile -> $i {
# say "LINE $i";
        # say "MATCH "~$i ~~ /http\s*/;

        if $i ~~ /http\s*\{/ {
            say "FOUND $i";
            @newfile.push($i,"jvm_path /usr/lib/default/jre/lib/amd64/server/libjvm.so;", "jvm_classpath");
#~/.m2/repository/nginx-clojure/nginx-clojure/*: ./src/main/java"
        } else {
            @newfile.push($i);
        }

        $j++;
    }


}
check_cmdline_args(@*ARGS);
