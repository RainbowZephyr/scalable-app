unit module insertion_generator;
our @names = ["Sara","Magda", "Sameer", "Sameh", "Samar", "Hadeel", "Samer", "Medhat","Salma","Sondos","Omar","Mohamed","Amgad","Menna","Lamees","Farah","Ismail","Magdy","Amal","Laila"];

our %ids = (Sara => 1, Magda => 2, Sameer => 3, Sameh => 4, Samar => 5, Hadeel => 6, Samer => 7, Medhat => 8, Salma => 9, Sondos => 10, Omar => 11, Mohamed => 12, Amgad => 13, Menna => 14, Lamees => 15, Farah => 16, Ismail => 17, Magdy => 18, Amal => 19, Laila => 20);

sub user_insertions is export {
    my @temp;
    my $c = 0;
    for @names -> $i {
        @temp.push("INSERT INTO member VALUES($c,'$i\@gmail.com','1', '$i','batee5', '1990-01-01', CURRENT_TIMESTAMP,CURRENT_TIMESTAMP);");
        @temp.push("INSERT INTO users VALUES($c);");
        $c++;
    }

    @temp.sort;
    return @temp;
}

sub tweets_insertions is export {
    my @temp;
    for %ids.kv -> $name , $id {
        for 1..20 {
            @temp.push("INSERT INTO tweets VALUES(DEFAULT, 'HELLO', $id, CURRENT_TIMESTAMP, NULL);");
        }
    }

    @temp.sort;
    return @temp;
}

sub followships_insertions is export {
    my @temp;
    for 1..20 -> $i {
        for 1..20 -> $j {
            next if ($i == $j);
            @temp.push("INSERT INTO followships VALUES(DEFAULT, $i, $j, true, now()::timestamp);");
            @temp.push("INSERT INTO followships VALUES(DEFAULT, $j, $i, true, now()::timestamp);");
        }
    }
    return @temp;
}

sub dm_insertions is export {
    my @temp;
    for 1..11 -> $i {
        for 1..11 -> $j {
            next if ($i == $j);
            next if (@temp.contains("INSERT INTO conversations VALUES(DEFAULT, $j, $i);"));
            @temp.push("INSERT INTO conversations VALUES(DEFAULT, $i, $j);");

            @temp.push("INSERT INTO direct_messages VALUES(DEFAULT, $i, $j, 'Hello', NULL, true, CURRVAL(pg_get_serial_sequence('conversations','id')), CURRENT_TIMESTAMP);");

            @temp.push("INSERT INTO direct_messages VALUES(DEFAULT, $j, $i, 'Hello', NULL, true, CURRVAL(pg_get_serial_sequence('conversations','id')), CURRENT_TIMESTAMP);");

        }
    }
    return @temp;
}

sub replies_insertions is export {
    my @temp;
    for 0..19 -> $i {
        @temp.push("INSERT INTO replies VALUES(DEFAULT, (SELECT id FROM tweets LIMIT 1 OFFSET " ~$i~ "), (SELECT id FROM tweets LIMIT 1 OFFSET " ~$i+1~ "), CURRENT_TIMESTAMP);");
    }

    return @temp;
}

sub retweets_insertions is export {
    my @temp;
    for 0..19 -> $i {
        @temp.push("INSERT INTO retweets VALUES(DEFAULT, (SELECT id FROM tweets LIMIT 1 OFFSET " ~$i~ "), (SELECT id FROM users LIMIT 1 OFFSET " ~$i~ "), (SELECT id FROM users LIMIT 1 OFFSET " ~$i+1~ "), CURRENT_TIMESTAMP);");
    }

    return @temp;
}

sub mentions_insertions is export {
    my @temp;
    for 1..20 -> $i {
        for 1..20 -> $j {
            next if ($i == $j);
            @temp.push("INSERT INTO tweets VALUES(DEFAULT, 'HELLO \@" ~@names[$j-1].gist.lc~ "', $i, now()::timestamp, NULL);");
        }
    }
    return @temp;
}

sub lists_insertions is export {
    my @temp;
    my $counter = 1;
    for 1..20 -> $i {
        @temp.push("INSERT INTO lists VALUES(DEFAULT, 'list$i', 'list', $i, false, now()::timestamp);");
        @temp.push("INSERT INTO subscriptions VALUES(DEFAULT, $i, $counter, now()::timestamp);");
        for 1..20 -> $j {
            next if ($i == $j);
            @temp.push("INSERT INTO memberships VALUES(DEFAULT, $j, $counter, now()::timestamp);");
        }
        $counter++;
    }
    return @temp;
}


say user_insertions;
