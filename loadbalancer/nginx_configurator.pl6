sub load_nginx_conf($path) {
    my $lines = slurp $path, :r;
    return $lines.split(/\n/).grep({!$_.contains("#")}).grep(/<-[^$]>/);
}

sub check_cmdline_args(@args) {

    my @options = ['-h', '-p', '-d', '-j', '-c', '-l'];

    if (@args.elems == 1 && @args[0] eqv "-h") {
        say "Option        Description\n-p            Enter path for load_balancer.Nginx config file default path is  /etc/nginx/nginx.conf\n-j            Enter jvm path default path is /usr/lib/default/jre/lib/amd64/server/libjvm.so\n-c            Enter class path default path is classpath ~/.m2/repository/nginx-clojure/nginx-clojure/0.4.4/*: ./target/classes\n-l            Enter the path for the nginx-clojure library default path is /usr/lib/nginx-mainline/modules/ngx_http_clojure_module.so\n-d            Generate file using defaults:\n                   jvm path: /usr/lib/default/jre/lib/amd64/server/libjvm.so\n                   nginx original config file: /etc/nginx/nginx.conf\n                   classpath ~/.m2/repository/nginx-clojure/nginx-clojure/0.4.4./*: CURRENT_DIR/target/classes";
    } elsif (@args.elems == 1 && @args[0] eqv "-d") {
        generate(Hash.new);
    } elsif @args.elems % 2 == 0 && @args.elems > 0 {
        if !(@args.pairup.hash.keys (-) @options).keys > 0 {
            my %hash = @args.pairup.hash;
            generate(%hash);
        } else {
            die "Unknown options: "~ @args.pairup.hash.keys (-) @options;
        }
    } else {
        die "Unknown options, use -h to view help";
    }
}

sub generate(%hash) {
    my @oldfile;
    my @newfile;
    my $classpath;
    my $jvm;
    my $load_module;

    if %hash<-p>:exists {
        @oldfile = load_nginx_conf(%hash<-p>);
    } else {
        @oldfile = load_nginx_conf("nginx.original");
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
        $cwd ~= "/";
        $classpath ="{$cwd}.m2/repository/nginx-clojure/nginx-clojure/0.4.4/*:{$*CWD.abspath}/target/classes";
    }

    if %hash<-j>:exists {
        $jvm = (%hash<-j>);
    } else {
        $jvm = "    /usr/lib/default/jre/lib/amd64/server/libjvm.so";
    }

    if %hash<-l>:exists {
        $load_module = (%hash<-l>);
    } else {
        $load_module = "/usr/lib/nginx-mainline/modules/ngx_http_clojure_module.so";
    }

    for @oldfile -> $i {
        if $i ~~ /http\s*\{/ {
            @newfile.push($i,"    jvm_path {$jvm};", "    jvm_classpath {$classpath};\n", "    upstream APPNAME \{","        server PATH;","        server PATH;", "    \}", "\tproxy_cache_path /tmp/cache levels=1:2 keys_zone=my_cache:10m max_size=10g inactive=60m use_temp_path=off;");
        } elsif $i eqv "include modules/enabled/*.conf;" {
            @newfile.push($i, "load_module \"{$load_module}\";");
        }  else {
            @newfile.push($i);
        }
        # say "READING LINE $i";
        # say "MATCHING " ~ ($i ~~ /location\s+\/\s+\{\n[.|\n]+\}/);
    }

    my $file = @newfile.join("\n");
    $file ~~ s/location\s+\/\s+\{\n[\w|\n|\;|\s|\/|\.]+\}/location \/ \{\n\t\tproxy_pass http:\/\/APPNAME;\n\t\tproxy_cache my_cache;\n\t\}/;
    # say $file;
    # @newfile>>.say;
    spurt "new_nginx.conf", $file;
    say "Please adjust the server names by replacing PATH\nTo use file, place it default nginx configuration file path, typically  /etc/nginx/nginx.conf and run `nginx -t` to check for errors";
}

check_cmdline_args(@*ARGS);
