# hanging_with_friends_solver
Solver for the once popular phone game "hanging with friends"

Java implementation; compile to a jar file and execute under the JRE.

## Solver

In this game, two players face off. Each crafts a word from their limited set of tiles for the other person to guess.

<img src="readme_imgs/hwf_createword.jpg" alt="Creating a word" width="150px">

When faced with this, it's best to make the hardest possible word for your friend. This solver doesn't attempt to define "hardest", but it does display a list of all possible words that can be created from the letters provided, ordered by length descending.

![Creating a word with the solver](readme_imgs/hwf_solver_createword.png)

Then, you must guess your opponent's word. With each guess, you are told which letters you got correct, and you may use the solver to continuously search for possible answers as you gain more information.

<img src="readme_imgs/hwf_guessword.jpg" alt="Guessing a word" width="150px">

The solver will do the work of eliminating impossible words based on the known constraints (length of word, letters you know, letters you've guessed). It also tells you which letters are present in the most remaining words - these are likely the best to guess.

![Guessing a word with the solver](readme_imgs/hwf_solver_guessword.png)
