my $erd = slurp "erd.erd";
my $output = open "database/schema2.sql", :w;

my @lines = $erd[0].split("\n");
my $type;
my $var;

for @lines -> $i {
    if ($i ~~ m/\[(\w+)\]/ ) {
        $output.say(");\n");
        $output.say("DROP TABLE IF EXISTS "~$0.lc ~ " CASCADE;");
        $output.say("CREATE TABLE " ~$0.lc~ " (");
    }

    if $i.contains("#Relationships") {
        exit;
    }

    $i ~~ m/\"(\w+)[\,|\"]/;
    $type = $0;

    if $i.contains("+") {
        $i ~~ (/\+\*?(\w+)/);
        $var = $0;
        $output.say("$var $type PRIMARY KEY,");

    } elsif $i.contains("*") && !$i.contains("+") {
        $i ~~ (/\*(\w+)\s+\{/);
        $var = $0;
        $output.say("$var $type,");

    }  elsif $i ~~ (/^\s*(\w+)\s+\{/) {
        $var = $0;
        $output.say("$var $type,");
    }
}

$output.close;
