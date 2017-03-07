
my $file;
our @names = ["Sara","Magda", "Sameer", "Sameh", "Samar", "Hadeel", "Samer", "Medhat","Salma","Sondos","Omar","Mohamed","Amgad","Menna","Lamees","Farah","Ismail","Magdy","Amal","Laila"];

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
        for 0..20 -> $id2 {
            if $id2 == $id1 {
                next;
            }

            $rand = (^3).pick;
            given $rand {
                when 0 {@temp.push("INSERT INTO friends VALUES($id1, $id2, false);")}
                when 1 {@temp.push("INSERT INTO friends VALUES($id1, $id2, true);")}
                when 2 {@temp.push("INSERT INTO friends VALUES($id1, $id2, null);")}
            }
        }
    }
    @temp.sort;
    return @temp;
}

sub post_insertions  {
    my @temp;
    my $c = 0;
    my $p = 0;

    #Writing on friends wall
    for 0..20 -> $id {
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


#
# sub dm_insertions  {
#     my @temp;
#     for 1..11 -> $i {
#         for 1..11 -> $j {
#             next if ($i == $j);
#             next if (@temp.contains("INSERT INTO conversations VALUES(DEFAULT, $j, $i);"));
#             @temp.push("INSERT INTO conversations VALUES(DEFAULT, $i, $j);");
#
#             @temp.push("INSERT INTO direct_messages VALUES(DEFAULT, $i, $j, 'Hello', NULL, true, CURRVAL(pg_get_serial_sequence('conversations','id')), CURRENT_TIMESTAMP);");
#
#             @temp.push("INSERT INTO direct_messages VALUES(DEFAULT, $j, $i, 'Hello', NULL, true, CURRVAL(pg_get_serial_sequence('conversations','id')), CURRENT_TIMESTAMP);");
#
#         }
#     }
#     return @temp;
# }
#
# sub replies_insertions  {
#     my @temp;
#     for 0..19 -> $i {
#         @temp.push("INSERT INTO replies VALUES(DEFAULT, (SELECT id FROM tweets LIMIT 1 OFFSET " ~$i~ "), (SELECT id FROM tweets LIMIT 1 OFFSET " ~$i+1~ "), CURRENT_TIMESTAMP);");
#     }
#
#     return @temp;
# }
#
# sub retweets_insertions  {
#     my @temp;
#     for 0..19 -> $i {
#         @temp.push("INSERT INTO retweets VALUES(DEFAULT, (SELECT id FROM tweets LIMIT 1 OFFSET " ~$i~ "), (SELECT id FROM users LIMIT 1 OFFSET " ~$i~ "), (SELECT id FROM users LIMIT 1 OFFSET " ~$i+1~ "), CURRENT_TIMESTAMP);");
#     }
#
#     return @temp;
# }
#
# sub mentions_insertions  {
#     my @temp;
#     for 1..20 -> $i {
#         for 1..20 -> $j {
#             next if ($i == $j);
#             @temp.push("INSERT INTO tweets VALUES(DEFAULT, 'HELLO \@" ~@names[$j-1].gist.lc~ "', $i, now()::timestamp, NULL);");
#         }
#     }
#     return @temp;
# }
#
# sub lists_insertions  {
#     my @temp;
#     my $counter = 1;
#     for 1..20 -> $i {
#         @temp.push("INSERT INTO lists VALUES(DEFAULT, 'list$i', 'list', $i, false, now()::timestamp);");
#         @temp.push("INSERT INTO subscriptions VALUES(DEFAULT, $i, $counter, now()::timestamp);");
#         for 1..20 -> $j {
#             next if ($i == $j);
#             @temp.push("INSERT INTO memberships VALUES(DEFAULT, $j, $counter, now()::timestamp);");
#         }
#         $counter++;
#     }
#     return @temp;
# }



# sub write_lists {
#     $file = open "database/@files[6]", :w;
#     lists_insertions.map({$file.say($_)});
#     $file.close;
# }
#
# sub write_dms {
#     $file = open "database/@files[0]", :w;
#     followships_insertions.unique.map({$file.say($_)});
#     dm_insertions.map({$file.say($_)});
#     $file.close;
# }
#
# sub write_all {
#     $file = open "database/@files[0]", :w;
#     followships_insertions.unique.map({$file.say($_)});
#     dm_insertions.map({$file.say($_)});
#     $file.close;
#
#     $file = open "database/@files[1]", :w;
#     mentions_insertions.map({$file.say($_)});
#     $file.close;
#
#     $file = open "database/@files[2]", :w;
#     replies_insertions.map({$file.say($_)});
#     $file.close;
#
#     $file = open "database/@files[3]", :w;
#     retweets_insertions.map({$file.say($_)});
#     $file.close;
#
#     $file = open "database/@files[4]", :w;
#     tweets_insertions.map({$file.say($_)});
#     $file.close;
#
#     $file = open "database/@files[5]", :w;
#     user_insertions.map({$file.say($_)});
#     $file.close;
#
#     $file = open "database/@files[6]", :w;
#     lists_insertions.map({$file.say($_)});
#     $file.close;
# }




# say post_insertions;

sub write_member {
    $file = open "database/users_insertions.sql", :w;
    member_insertions.map({$file.say($_)});
    $file.close;
}

sub write_post {
    $file = open "database/post_insertions.sql", :w;
    post_insertions.map({$file.say($_)});
    $file.close;
}

if @*ARGS.elems > 1 {
    say "Unknown run option";
} else {
    given @*ARGS {
        when "-a" {say "a"}
        when "-u" {say "u"}
        default {

            my $member = start write_member.eager;
            my $post = start write_post.eager;

            my $written_member = await $member;
            my $written_post = await $post;
        }
    }
}

# say @*ARGS;

# for @generation_functions -> &f {
    # say &f;
# }

# say @generation_functions[0].^methods;
#
# say @generation_functions[0].prec;
