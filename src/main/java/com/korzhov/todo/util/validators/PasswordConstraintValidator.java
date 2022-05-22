package com.korzhov.todo.util.validators;

import com.google.common.base.Joiner;
import com.korzhov.todo.util.validators.annotation.Password;
import org.passay.CharacterOccurrencesRule;
import org.passay.CharacterRule;
import org.passay.DictionaryRule;
import org.passay.EnglishCharacterData;
import org.passay.HistoryRule;
import org.passay.LengthRule;
import org.passay.PasswordData;
import org.passay.PasswordValidator;
import org.passay.RuleResult;
import org.passay.WhitespaceRule;
import org.passay.dictionary.ArrayWordList;
import org.passay.dictionary.WordListDictionary;
import org.passay.dictionary.sort.ArraysSort;

import java.util.Arrays;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PasswordConstraintValidator implements ConstraintValidator<Password, String> {

  private static final WordListDictionary dictionary =
      new WordListDictionary(new ArrayWordList(new String[]{
          "password", "qwerty123", "Password123"}, true, new ArraysSort()));

  @Override
  public boolean isValid(String password, ConstraintValidatorContext context) {
    PasswordValidator passwordValidator = new PasswordValidator(Arrays.asList(
        new LengthRule(8, 30),
        new CharacterOccurrencesRule(3),
        new CharacterRule(EnglishCharacterData.Alphabetical),
        new CharacterRule(EnglishCharacterData.LowerCase),
        new CharacterRule(EnglishCharacterData.UpperCase, 1),
        new CharacterRule(EnglishCharacterData.Digit, 1),
        new HistoryRule(),
        new DictionaryRule(dictionary),
        new WhitespaceRule()));

    RuleResult result = passwordValidator.validate(new PasswordData(password));
    if (result.isValid()) {
      return true;
    }

    context.disableDefaultConstraintViolation();
    context.buildConstraintViolationWithTemplate(
        Joiner.on(",").join(passwordValidator.getMessages(result))
    ).addConstraintViolation();

    return false;
  }

}
