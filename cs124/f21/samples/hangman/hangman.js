var canvas;
var g;
var nextWordBtn;
var cheatBtn;
var letterBtns;
var gameOver;
var cheated;
var guesses;
var badGuesses;
var message;
var word;

var wordlist = [ // words randomly selected from a dictionary file, lengths 5 to 11
    "academic",
    "acclimate",
    "adhesive",
    "admitting",
    "adoring",
    "adverbial",
    "amicable",
    "anagram",
    "antipasto",
    "apathy",
    "aping",
    "appeased",
    "apposite",
    "approvals",
    "arrays",
    "artery",
    "atmosphere",
    "attitudes",
    "awash",
    "backbone",
    "bagels",
    "balloon",
    "barbarian",
    "barber",
    "based",
    "batting",
    "bearded",
    "bears",
    "beggarly",
    "beige",
    "bellyful",
    "bided",
    "bivalve",
    "blinds",
    "blowgun",
    "bootie",
    "booting",
    "borrowing",
    "bossy",
    "botanist",
    "bratty",
    "breakups",
    "brokering",
    "broomstick",
    "buckle",
    "bugling",
    "buildings",
    "bulbous",
    "bullet",
    "burial",
    "busyness",
    "caddied",
    "caffeine",
    "calibrate",
    "campy",
    "cannon",
    "capably",
    "carded",
    "celesta",
    "certifying",
    "chaise",
    "changing",
    "charmin",
    "chiffon",
    "chilli",
    "chose",
    "cigarettes",
    "cinders",
    "cleaners",
    "clergy",
    "clouts",
    "cockpits",
    "colloid",
    "colonial",
    "comeuppance",
    "communal",
    "compensate",
    "conceits",
    "concierge",
    "condominium",
    "conjugate",
    "conscience",
    "contend",
    "contractor",
    "convexity",
    "corporal",
    "corrupted",
    "coterie",
    "counselors",
    "couriers",
    "covenant",
    "crackles",
    "cranky",
    "crossbeams",
    "crouched",
    "cruelest",
    "cubit",
    "curators",
    "curvaceous",
    "cybernetics",
    "cypress",
    "deadlocking",
    "deafen",
    "decompress",
    "decoys",
    "decree",
    "deficits",
    "delved",
    "demean",
    "demitass",
    "deranged",
    "derivative",
    "desecrated",
    "diaries",
    "dicker",
    "diphtheria",
    "disapproval",
    "disobeyed",
    "disowning",
    "disparage",
    "dispels",
    "distention",
    "dotage",
    "drizzle",
    "dulcimer",
    "dwindled",
    "edginess",
    "educable",
    "eeriness",
    "elbows",
    "emperor",
    "emulate",
    "emulator",
    "endowment",
    "enfeebled",
    "engulfing",
    "enrolment",
    "enslaves",
    "enrich",
    "essential",
    "evinced",
    "explicating",
    "expressive",
    "fains",
    "false",
    "falseness",
    "feistier",
    "fence",
    "fishy",
    "flapjacks",
    "flaunting",
    "fledglings",
    "fleetest",
    "flirtation",
    "flyswatter",
    "foosball",
    "fostering",
    "foundry",
    "fours",
    "fridges",
    "fringes",
    "frisks",
    "furtively",
    "fuzing",
    "gaunted",
    "genies",
    "germination",
    "gilded",
    "glossiest",
    "gowns",
    "granularity",
    "gruelling",
    "guilty",
    "guttural",
    "hamiltonian",
    "hammer",
    "handballs",
    "handgun",
    "harming",
    "headdress",
    "headstone",
    "hearkened",
    "highjacked",
    "hightail",
    "hogwash",
    "honchos",
    "horsewhip",
    "housebroken",
    "huckleberry",
    "hunger",
    "humanizing",
    "hungrily",
    "hyperbola",
    "impiously",
    "impulsive",
    "inculpates",
    "inducements",
    "informative",
    "inked",
    "innovation",
    "inquiring",
    "insoles",
    "instil",
    "integral",
    "interdicted",
    "intermezzos",
    "interracial",
    "invigorated",
    "iroquois",
    "irrelevancy",
    "jealous",
    "jilted",
    "jingle",
    "joyless",
    "juxtaposing",
    "khaki",
    "kibitzed",
    "kicker",
    "kidder",
    "kilobyte",
    "kimberley",
    "knoll",
    "knows",
    "layout",
    "lectures",
    "libreville",
    "licence",
    "licking",
    "lineups",
    "lionize",
    "livens",
    "located",
    "loopiest",
    "lubricator",
    "luncheoned",
    "mains",
    "malice",
    "maliciously",
    "manicures",
    "manifold",
    "mansion",
    "mantel",
    "mantra",
    "marital",
    "marsala",
    "matchmakers",
    "meekness",
    "mendicant",
    "merlot",
    "migrated",
    "misdealt",
    "modelled",
    "monikers",
    "monogamous",
    "monotonous",
    "moody",
    "mulberry",
    "muscled",
    "musicology",
    "namely",
    "napkin",
    "needle",
    "nicest",
    "noise",
    "nostalgia",
    "nought",
    "nudging",
    "omniscient",
    "orates",
    "outstrip",
    "overawe",
    "overgrowth",
    "overshot",
    "pacifist",
    "palate",
    "panelings",
    "paramour",
    "parrots",
    "paunchy",
    "peanut",
    "pepsi",
    "pervaded",
    "pictorial",
    "pikers",
    "pirate",
    "pissed",
    "planes",
    "plantations",
    "plumbing",
    "poisons",
    "prairie",
    "precipices",
    "precluded",
    "pretenses",
    "probation",
    "propagating",
    "pucker",
    "puffballs",
    "pugilism",
    "reeducated",
    "reexamining",
    "regulation",
    "relatives",
    "releasable",
    "resistance",
    "retools",
    "rifling",
    "ringlet",
    "roaring",
    "ruffle",
    "satisfying",
    "saucepan",
    "scanning",
    "schoolchild",
    "schoolwork",
    "scotching",
    "scuffle",
    "seascapes",
    "seceding",
    "secrete",
    "separator",
    "sequential",
    "serfs",
    "settle",
    "seventieth",
    "shaker",
    "sheepish",
    "shied",
    "shoveled",
    "shovelful",
    "showgirl",
    "shrilled",
    "shrubbery",
    "sidelining",
    "similarly",
    "sincerest",
    "singular",
    "sizeable",
    "skits",
    "skittering",
    "sleepwalked",
    "sloppiest",
    "smuggle",
    "snowy",
    "sociable",
    "softener",
    "souths",
    "spares",
    "spent",
    "spicier",
    "spoons",
    "squawked",
    "staler",
    "stancher",
    "stanford",
    "steels",
    "stinging",
    "stirrings",
    "students",
    "stupid",
    "sturdy",
    "sublime",
    "suffusing",
    "sulkily",
    "summonses",
    "sunken",
    "sunny",
    "surrounding",
    "suspends",
    "swirl",
    "taffy",
    "tension",
    "theocracies",
    "theology",
    "theorize",
    "timber",
    "timid",
    "toasted",
    "touting",
    "traffics",
    "transposing",
    "treasurer",
    "tributaries",
    "triteness",
    "trudge",
    "trumpeting",
    "trundle",
    "trying",
    "turnout",
    "twerp",
    "unalterably",
    "unceasingly",
    "underpay",
    "uneasy",
    "uniformity",
    "unsaddle",
    "unsparing",
    "until",
    "untrue",
    "usurped",
    "vaguer",
    "venturing",
    "verily",
    "victimizes",
    "vineyards",
    "vitiates",
    "waggle",
    "wangles",
    "wattled",
    "weakened",
    "wearable",
    "wheel",
    "where",
    "whisker",
    "whistle",
    "wicket",
    "wildfires",
    "windlass",
    "wintriest",
    "wither",
    "worries",
    "worsened",
    "worsted",
    "writen",
    "zipper"
];

function startGame() {
		gameOver = false;
		cheated = false;
		guesses = "";
		badGuesses = 0;
		nextWordBtn.disabled = (true);
		for (var i = 0; i < letterBtns.length; i++) {
			letterBtns[i].disabled = (false);
		}
		cheatBtn.disabled = (false);
      var index = Math.floor(wordlist.length * Math.random());
		word = wordlist[index].toUpperCase();
      wordlist.splice(index,1);
		message = "The word has " + word.length + " letters.  Let's play Hangman!";
		draw();
}

function doCheat() {
		cheated = true;
		var goodLetters = "";  // The remaining correct but unguessed letters.
      var alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
      for (var i = 0; i < 26; i++) {
         var ch = alphabet.charAt(i);
			if (word.indexOf(ch) >= 0 && guesses.indexOf(ch) == -1) {
				// ch is a letter in the word, and has not yet been guessed by the user.
				goodLetters += ch;
			}
		}
		var index; // A random index into goodLetters, for picking a random uplayed letter.
		index = Math.floor(goodLetters.length*Math.random());
		var letter = goodLetters.charAt(index);
		guesses += letter;
		if (wordIsComplete()) {
			// The game has ended because the free letter completed the word.
			message = "The word is complete.  You win (but you cheated)!";
			cheatBtn.disabled =(true);
			nextWordBtn.disabled =(false);
			for (var j = 0; j < 26; j++)
            letterBtns[j].disabled = true;
			gameOver = true;
		}
		else {
			var btn = letterBtns[ letter.charCodeAt(0) - 65 ];
			btn.disabled = (true);
			message = "Your free letter is:  " + letter;
		}
		draw();
}

function doLetterBtn(evt) {
		var guess; // Which letter was clicked, the first (and only) char in the button text.
		guess = evt.target.innerHTML;
      console.log(guess);
		guesses = guesses + guess; // Add this letter to the list of letters guessed so far.
		evt.target.disabled = true; // Disable button so that it can't be clicked again in this game.
		if (wordIsComplete()) {  // Game ends because all letters in the word have been guessed.
			if (cheated)
				message = "The word is complete.  You win (but you cheated)!";
			else
				message = "Congratulations!  You got it!";
			cheatBtn.disabled = (true);
			nextWordBtn.disabled = (false);
			for (var i = 0; i < 26; i++)
				letterBtns[i].disabled = (true);
			gameOver = true;
		}
		else if (word.indexOf(guess) >= 0) {  // Letter is in the word.
			message = "Yes, " + guess + " is in the word.  Pick your next letter.";
		}
		else {  // Letter is not in the word, so number of bad guesses goes up by one.
			badGuesses++;
			if (badGuesses == 7) { // Game ends becasue the user is out of guesses.
				message = "Sorry, you're hung!  The word is:  " + word;
				cheatBtn.disabled = (true);
				nextWordBtn.disabled = (false);
				for (var j = 0; j < 26; j++)
					letterBtns[j].disabled = (true);
				gameOver = true;
			}
			else {
				message = "Sorry, " + guess + " is not in the word.  Pick your next letter.";
			}
		}
		draw();
}

function draw() {
      g.fillStyle = "rgb(250,230,180)";
		g.fillRect(0, 0, 650, 420);
		g.font = "20px Arial";
		if (message != null) {
			g.fillStyle = "red";
			g.fillText(message, 30, 40);
		}
		if ( gameOver == true ) {
			g.fillStyle = "blue";
			g.fillText("Click \"Next word\" to play again.",30,70);
		}
		else {
			g.fillText("Bad Guesses Remaining:  " + (7-badGuesses), 30,70);
		}
		drawHangman(badGuesses);
		g.strokeStyle = "#333333";
		g.fillStyle = "#333333";
      g.lineWidth = 4;
		g.font = "30px Arial";
		for (var i = 0; i < word.length; i++) {
         g.beginPath();
         g.moveTo(10+i*50,400);
         g.lineTo(50+i*50,400);
         g.stroke();
			if ( guesses.indexOf(word.charAt(i)) >= 0 ) {
				g.fillText("" + word.charAt(i), 20 + i*50, 395);
			}
		}
}

function drawHangman(level) {
		g.fillStyle = "rgb(90,30,0)";
		g.strokeStyle = "rgb(90,30,0)";
      g.lineWidth = 3;
		g.fillRect(300,320,200,10);
		g.fillRect(340,100,10,225);
		g.fillRect(340,120,100,7);
      g.beginPath();
      g.moveTo(430,125);
      g.lineTo(430,160);
      g.stroke();
      if (level == 0)
         return;
		g.fillStyle = "rgb(0,70,0)";
		g.fillRect(415,140,30,40);
		g.strokeStyle = "rgb(0,70,0)";
      g.lineWidth = 6;
      g.beginPath();
		if (level > 1) {
			g.moveTo(430,180); g.lineTo(430,205);
		}
		if (level > 2) {
			g.moveTo(395,225); g.lineTo(430,200);
		}
		if (level > 3) {
			g.moveTo(430,200); g.lineTo(465,225);
		}
		if (level > 4) {
			g.moveTo(430,205); g.lineTo(430,245);
		}
		if (level > 5) {
			g.moveTo(390,290); g.lineTo(430,245);
		}
		if (level > 6) {
			g.moveTo(430,245); g.lineTo(470,290);
		}
      g.stroke();
}

function wordIsComplete() {
		for (var i = 0; i < word.length; i++) {
			var ch = word.charAt(i);
			if ( guesses.indexOf(ch) == -1 ) {
				return false;
			}
		}
		return true;
}

function init() {
    try {
        canvas = document.getElementById("canvas");
        g = canvas.getContext("2d");
    }
    catch (e) {
        document.getElementById("headline").innerHTML =
            "Sorry, this page requires HTML canvas graphics.";
        return;
    }
    g.lineCap = "round";
    letterBtns = [];
    var alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    for (var i = 0; i < 26; i++) {
       var letter = alphabet.charAt(i);
       document.getElementById("btn" + letter).onclick = doLetterBtn;
       letterBtns.push(document.getElementById("btn" + letter));
    }
    nextWordBtn = document.getElementById("next");
    nextWordBtn.onclick = startGame;
    cheatBtn = document.getElementById("cheat");
    cheatBtn.onclick = doCheat;
    startGame();
}

window.onload = init;