sub load_nginx_conf($path) {
    my $lines = slurp $path, :r;
    return $lines.split(/\n/).grep({!$_.contains("#")}).grep(/<-[^$]>/);
}

sub check_cmdline_args(@args) {

    my @options = ['-h', '-p', '-d', '-j', '-c', '-l'];

    if (@args.elems == 1 && @args[0] eqv "-h") {
        say "Option        Description\n-p            Enter path for Nginx config file default path is  /etc/nginx/nginx.conf\n-j            Enter jvm path default path is /usr/lib/default/jre/lib/amd64/server/libjvm.so\n-c            Enter class path default path is classpath ~/.m2/repository/nginx-clojure/nginx-clojure/*: ./src/main/java\n-l            Enter the path for the nginx-clojure library default path is /usr/lib/nginx-mainline/modules/ngx_http_clojure_module.so\n-d            Generate file using defaults:\n                   jvm path: /usr/lib/default/jre/lib/amd64/server/libjvm.so\n                   nginx original config file: /etc/nginx/nginx.conf\n                   classpath ~/.m2/repository/nginx-clojure/nginx-clojure/*: CURRENT_DIR/src/main/java";
    } elsif (@args.elems == 1 && @args[0] eqv "-d") {
        generate_defaults;
    } elsif @args.elems % 2 == 0 {
        if !(@args.pairup.hash.keys (-) @options).keys > 0 {
            my %hash = @args.pairup.hash;
            generate(%hash);
        } else {
            die "Unknown options: "~ @args.pairup.hash.keys (-) @options;
        }
    } else {
        die "Unknown options"
    }
}

sub generate(%hash) {
    my @oldfile;
    my $classpath;
    if %hash<-p>:exists {
        @oldfile = load_nginx_conf(%hash<-p>);
    } else {
        @oldfile = load_nginx_conf("/etc/nginx/nginx.conf");
    }

    if %hash<-c>:exists {
        $classpath = %hash<-c>;
    } else {
        my $cwd;
        if ($*KERNEL.name eqv "win32") {
            $cwd = $*CWD.abspath ~~ m/(C:\/\/Users\/[\w|\s]+)/;
        } else {
            $cwd = $*CWD.abspath ~~ m/(\/home\/[\w|\s]+)/;
        }
        $classpath ="{$cwd}.m2/repository/nginx-clojure/nginx-clojure:{$*CWD.abspath}/src/main/java";
    }
}

sub generate_defaults() {
    my @oldfile = load_nginx_conf("/etc/nginx/nginx.conf");
    my @newfile;
    my $cwd;
    if ($*KERNEL.name eqv "win32") {
        $cwd = $*CWD.abspath ~~ m/(C:\/\/Users\/[\w|\s]+)/;
    } else {
        $cwd = $*CWD.abspath ~~ m/(\/home\/[\w|\s]+)/;
    }

    $cwd ~= "/";

    for @oldfile -> $i {
        if $i ~~ /http\s*\{/ {
            @newfile.push($i,"    jvm_path /usr/lib/default/jre/lib/amd64/server/libjvm.so;", "    jvm_classpath {$cwd}.m2/repository/nginx-clojure/nginx-clojure:{$*CWD.abspath}/src/main/java\n", "    upstream APPNAME \{","        server PATH;","        server PATH;", "    \}");
        } elsif $i eqv "include modules/enabled/*.conf;" {
            @newfile.push($i, "load_module \"/usr/lib/nginx-mainline/modules/ngx_http_clojure_module.so\";");
        }  else {
            @newfile.push($i);
        }
        # say "READING LINE $i";
        # say "MATCHING " ~ ($i ~~ /location\s+\/\s+\{\n[.|\n]+\}/);
    }
    my $file = @newfile.join("\n");
    $file ~~ s/location\s+\/\s+\{\n[\w|\n|\;|\s|\/|\.]+\}/location \/ \{\n\tproxy_pass http:\/\/APPNAME;\n\}/;
    # say $file;
    @newfile>>.say;
    # spurt "new_nginx.conf", $file;
    # say "Please adjust the server names by replacing PATH\nTo use file, place it default nginx configuration file path, typically  /etc/nginx/nginx.conf and run `nginx -t` to check for errors";
}

check_cmdline_args(@*ARGS);
