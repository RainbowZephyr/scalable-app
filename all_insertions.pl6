my $current_path;
my $path;
my @postgres_conf;
if (($*KERNEL.name) === "win32") {
    $current_path = qx/cd/;
    $path = $current_path.substr(0,$current_path.chars-1);
    say "PATH: $path";
    @postgres_conf = ($path~"\\Postgres.conf").IO.lines;
} else {
    $current_path = qx/pwd/;
    $path = $current_path.substr(0,$current_path.chars-1);
    @postgres_conf = ($path~"/Postgres.conf").IO.lines;
}

my $username;
my $database;
my $password;

for @postgres_conf -> $line {
    if $line.contains("username") {
        $line ~~ (/\[(.+)\]/);
        $username = $0;
    }

    if $line.contains("password") {
        $line ~~ (/\[(.+)\]/);
        $password = $0;
    }

    if $line.contains("database") {
        $line ~~ (/\[(.+)\]/);
        $database = $0;
    }
}

my @sql_files = ["schema.sql","users_insertions.sql","post_insertions.sql", "group_insertions.sql", "friends_insertions.sql", "message_insertions.sql", "user_procs.sql"];

my @procs = ["group_procedures.sql", "user_procs.sql"];

if (($*KERNEL.name) === "win32") {
    my $databases = qqx/psql -l -U $username/;
    if $databases.contains($database) {
        shell "psql -c \"drop schema public cascade\" -d $database -U $username";
        shell "psql -c \"create schema public\" -d $database -U $username ";
    } else {
        shell "psql -c \"create database $database owner $username\" -d postgres -U $username";
    }

    for @sql_files -> $file {
        say "Inserting $file";
        shell "psql -f {$path}/database/$file  -d $database -U $username";
    }

} else {

    if (@*ARGS > 0) {
        given @*ARGS {
            when "-p" {
                say "Procs Only";
                for @procs -> $file {
                    say "Inserting $file";
                    shell "PGPASSWORD=$password psql $database $username -f {$path}/database/$file";
                }

            }
        }
    } else {
        my $databases = qx/psql -l/;
        if $databases.contains($database) {
            shell "psql -c \"drop schema public cascade\" $database $username";
            shell "PGPASSWORD=$password psql -c \"create schema public\" $database $username ";
        } else {
            shell "psql -c \"create database $database owner $username\" postgres $username";
        }

        for @sql_files -> $file {
            say "Inserting $file";
            shell "PGPASSWORD=$password psql $database $username -f {$path}/database/$file";
        }
    }
}
