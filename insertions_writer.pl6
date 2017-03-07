
# Admins are Bob, Sameeha and Aseer el A7zan
our %ids = (Sara => 1, Magda => 2, Sameer => 3, Sameh => 4, Samar => 5, Hadeel => 6, Samer => 7, Medhat => 8, Salma => 9, Sondos => 10, Omar => 11, Mohamed => 12, Amgad => 13, Menna => 14, Lamees => 15, Farah => 16, Ismail => 17, Magdy => 18, Amal => 19, Laila => 20, Bob => 21, Sameeha => 22, "Aseer el A7zan" => 23);

our %ids_without_admins = (Sara => 1, Magda => 2, Sameer => 3, Sameh => 4, Samar => 5, Hadeel => 6, Samer => 7, Medhat => 8, Salma => 9, Sondos => 10, Omar => 11, Mohamed => 12, Amgad => 13, Menna => 14, Lamees => 15, Farah => 16, Ismail => 17, Magdy => 18, Amal => 19, Laila => 20);

sub member_insertions  {
    my @temp;
    for %ids.kv -> $name , $id {
        @temp.push("INSERT INTO member VALUES($id,'$name\@gmail.com','1', '$name','batee5', '1990-01-01', CURRENT_TIMESTAMP,CURRENT_TIMESTAMP);");

        if $id < 21 {
            @temp.push("INSERT INTO users VALUES($id);");
        } else {
            @temp.push("INSERT INTO admin VALUES($id);");
        }
    }

    # @temp.sort;
    return @temp;
}

sub friends_insertions  {
    my @temp;
    my $rand;
    for %ids.kv -> $name1 , $id1 {
        for 1..20 -> $id2 {
            next if $id2 == $id1;

            $rand = (^3).pick;
            given $rand {
                when 0 {@temp.push("INSERT INTO friends VALUES($id1, $id2, false);")}
                when 1 {@temp.push("INSERT INTO friends VALUES($id1, $id2, true);")}
                when 2 {@temp.push("INSERT INTO friends VALUES($id1, $id2, null);")}
            }
        }
    }

    return @temp;
}

sub post_insertions  {
    my @temp;
    my $c = 1;
    my $p = 1;

    #Writing on friends wall
    for 1..20 -> $id {
        for %ids_without_admins.kv -> $name2, $id2 {
            @temp.push("INSERT INTO post VALUES ($p, $id, $id2, 'Hello, I am $name2', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);");

            if $id2 != $id {
                @temp.push("INSERT INTO comment VALUES ($c, $p, $id2, 'Hey!', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);");
                $c++;
            }
            $p++;
        }
    }
    return @temp;
}

sub message_insertions  {
    my @temp;
    my $mt = 1;

    #id1 sends to $id2 and vice versa
    for 1..20 -> $id1, $id2 {

        @temp.push("INSERT INTO message_thread VALUES ($mt, CURRENT_TIMESTAMP);");

        #add both users to thread
        @temp.push("INSERT INTO message_thread_users VALUES ($mt, $id1);");
        @temp.push("INSERT INTO message_thread_users VALUES ($mt, $id2);");

        @temp.push("INSERT INTO message VALUES (DEFAULT, $mt, $id1, 'Yo1!', CURRENT_TIMESTAMP);");
        @temp.push("INSERT INTO message VALUES (DEFAULT, $mt, $id2, 'Yo2!', CURRENT_TIMESTAMP);");

        $mt++;

    }

    #Group chat of all members
    @temp.push("INSERT INTO message_thread VALUES ($mt, CURRENT_TIMESTAMP);");

    for 1..20 -> $id {
        @temp.push("INSERT INTO message_thread_users VALUES ($mt, $id);");

        @temp.push("INSERT INTO message VALUES (DEFAULT, $mt, $id, 'Yo! (ID): $id', CURRENT_TIMESTAMP);");
    }

    return @temp;
}

sub group_insertions  {
    my @temp;
    my $g = 1;

    @temp.push("INSERT INTO facebook_group VALUES ($g, 'All of us', 'A group to bring us all togther', 1, CURRENT_TIMESTAMP);");
    @temp.push("INSERT INTO group_members VALUES ($g,  1, TRUE);");
    @temp.push("INSERT INTO group_post VALUES (DEFAULT,  $g, 1, 'hello everyone', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);");


    for 2..20 -> $id {
        @temp.push("INSERT INTO group_members VALUES ($g,  $id, FALSE);");
    }

    $g++;
    @temp.push("INSERT INTO facebook_group VALUES ($g, 'Drama', 'Drama Queens 101', 2,  CURRENT_TIMESTAMP);");
    @temp.push("INSERT INTO group_members VALUES ($g,  2, TRUE);");
    @temp.push("INSERT INTO group_members VALUES ($g,  3, TRUE);");

    for 4..12 -> $id {
        @temp.push("INSERT INTO group_members VALUES ($g,  $id, FALSE);");
    }

    @temp.push("INSERT INTO group_post VALUES (DEFAULT,  $g, 2, 'el ehtemam mabytelebsh', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);");

    return @temp;
}


sub write_member {
    my $file = open "database/users_insertions.sql", :w;
    member_insertions.map({$file.say($_)});
    $file.close;
}

sub write_post {
    my $file = open "database/post_insertions.sql", :w;
    post_insertions.map({$file.say($_)});
    $file.close;
}

sub write_friends {
    my $file = open "database/friends_insertions.sql", :w;
    friends_insertions.map({$file.say($_)});
    $file.close;
}

sub write_messags {
    my $file = open "database/message_insertions.sql", :w;
    message_insertions.map({$file.say($_)});
    $file.close;
}

sub write_groups {
    my $file = open "database/group_insertions.sql", :w;
    group_insertions.map({$file.say($_)});
    $file.close;
}

if @*ARGS.elems > 1 {
    die "Unknown run option, please choose from list below or leave empty to generate all insertions";
} else {
    given @*ARGS {
        default {
            write_member;
            write_post;
            write_friends;
            write_messags;
            write_groups;
        }
    }
}
