package com.example.shippingbotserver.service;

import com.example.shippingbotserver.contants.Constants;
import com.example.shippingbotserver.entity.Attitude;
import com.example.shippingbotserver.entity.Counter;
import com.example.shippingbotserver.entity.User;
import com.example.shippingbotserver.dto.UserDto;
import com.example.shippingbotserver.exceptions.EmptyListUsersException;
import com.example.shippingbotserver.exceptions.UserNotFoundException;
import com.example.shippingbotserver.repository.AttitudeRepository;
import com.example.shippingbotserver.repository.UserRepository;
import com.example.shippingbotserver.service.interfaces.Drawer;
import com.example.shippingbotserver.service.interfaces.DtoConverter;
import com.example.shippingbotserver.service.interfaces.Translator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final AttitudeRepository attitudeRepository;
    private final Translator translateService;
    private final Drawer drawerService;
    private final DtoConverter dtoService;

    public UserDto findUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(UserNotFoundException::new);
        if (user.getName() != null && user.getPreference() != null && user.getDescription() != null) {
            translateService.translate(user);
            return dtoService.convert(drawerService.draw(user), user, "");
        }
        return dtoService.convert(null, user, "");
    }

    public UserDto search(Long id) {
        User user = userRepository.findById(id).orElseThrow(UserNotFoundException::new);
        try {
            User res = searchNext(user);
            translateService.translate(searchNext(user));
            return dtoService.convert(drawerService.draw(res), res, statusInit(user, res));
        } catch (EmptyListUsersException e) {
            return dtoService.convertEmpty(drawerService.draw(null));
        }
    }

    public UserDto getFavorite(Long id, String action) {
        User user = userRepository.findById(id).orElseThrow(UserNotFoundException::new);
        try {
            User res = getUserInFavorites(user, action.equals(Constants.RIGHT.getName()));
            String status = statusInit(user, res);
            translateService.translate(res);
            return dtoService.convert(drawerService.draw(res), res, status);
        } catch (EmptyListUsersException e) {
            return dtoService.convertEmpty(drawerService.draw(null));
        }
    }

    public UserDto attitudePush(User id, String attitude) {
        try {
            User user = userRepository.findById(id.getId()).orElseThrow(UserNotFoundException::new);
            User userSearch = pushLike(user, attitude);
            String status = statusInit(user, userSearch);
            userSearch = searchNext(user);
            translateService.translate(userSearch);
            return dtoService.convert(drawerService.draw(userSearch), userSearch, status);
        } catch (EmptyListUsersException e) {
            return dtoService.convertEmpty(drawerService.draw(null));
        }
    }

    private User pushLike(User user, String attitude) throws EmptyListUsersException {
        User userSearch = searchNext(user);
        attitudeRepository.save(new Attitude(user, userSearch, attitude));
        return userSearch;
    }

    public void saveLover(User user) {
        translateGenderAndPreference(user);
        userRepository.save(user);
    }

    public void deleteUser(Long id) {
        try {
            User user = userRepository.findById(id).orElseThrow(UserNotFoundException::new);
            deleteAttitudes(user);
        } catch (UserNotFoundException ignore) {
        }
    }

    private void deleteAttitudes(User user) {
        attitudeRepository.deleteAll(
                Stream.concat(
                                attitudeRepository.findAllByTargetId(user)
                                        .stream(),
                                attitudeRepository.findAllByInitId(user)
                                        .stream())
                        .collect(Collectors.toList())
        );
    }

    public void update(User user) {
        try {
            User original = userRepository.findById(user.getId()).orElseThrow(UserNotFoundException::new);
            user.setCounter(original.getCounter());
        } catch (UserNotFoundException ignore) {
        }
        translateGenderAndPreference(user);
        userRepository.save(user);
        updateAttitudes(user);
    }

    private void updateAttitudes(User user) {
        if (user.getPreference() == null || user.getGender() == null) {
            return;
        }
        List<Attitude> attitudes = Stream.concat(
                attitudeRepository.findAllByInitId(user)
                        .stream()
                        .filter(a -> !isPreference(user, a.getTargetId())),
                attitudeRepository.findAllByTargetId(user)
                        .stream()
                        .filter(a -> !isPreference(a.getInitId(), user))).collect(Collectors.toList());
        attitudeRepository.deleteAll(attitudes);
    }

    private void translateGenderAndPreference(User user) {
        if (user.getGender() != null) {
            if (user.getGender().equals(Constants.SUDAR_MALE.getName()) || user.getGender().equals(Constants.BOY.getName())) {
                user.setGender(Constants.BOY.getName());
            } else {
                user.setGender(Constants.GIRL.getName());
            }
        }
        if (user.getPreference() != null) {
            if (user.getPreference().toLowerCase(Locale.ROOT).equals("сударя")
                    || user.getPreference().equals(Constants.BOY.getName())) {
                user.setPreference(Constants.BOY.getName());
            } else if (user.getPreference().toLowerCase(Locale.ROOT).equals("всех")) {
                user.setPreference(Constants.ALL.getName());
            } else {
                user.setPreference(Constants.GIRL.getName());
            }
        }
    }

    private String statusInit(User user, User showing) {
        Attitude like = attitudeRepository.findByInitIdAndTargetIdAndNameOfAction(user, showing, Constants.LIKE.getName());
        Attitude userLike = attitudeRepository.findByInitIdAndTargetIdAndNameOfAction(showing, user, Constants.LIKE.getName());
        if (like != null && userLike != null) {
            return Constants.MATCH.getName();
        } else if (userLike == null && like != null) {
            return Constants.LOVE_YOU.getName();
        } else if (userLike != null) {
            return Constants.YOU_ARE_LOVED.getName();
        }
        return "";
    }

    private User searchNext(User user) throws EmptyListUsersException {
        List<User> userAttitudes = attitudeRepository.findAllByInitId(user)
                .stream().map(Attitude::getTargetId).collect(Collectors.toList());
        List<User> userList = userRepository.findAll();
        return userList.stream()
                .filter(u -> !u.getId().equals(user.getId()))
                .filter(u -> !userAttitudes.contains(u))
                .filter(u -> isPreference(user, u))
                .findFirst().orElseThrow(EmptyListUsersException::new);
    }


    private User getUserInFavorites(User user, boolean increment) throws EmptyListUsersException {
        List<User> allAttitudes = Stream.concat(
                        attitudeRepository.findAllByInitIdAndNameOfAction(user, Constants.LIKE.getName())
                                .stream()
                                .map(Attitude::getTargetId)
                                .filter(u -> !u.getId().equals(user.getId())),
                        attitudeRepository.findAllByTargetIdAndNameOfAction(user, Constants.LIKE.getName())
                                .stream()
                                .map(Attitude::getInitId)
                                .filter(u -> !u.getId().equals(user.getId())))
                .distinct()
                .collect(Collectors.toList());
        if (allAttitudes.isEmpty()) {
            throw new EmptyListUsersException();
        }
        if (user.getCounter() == null) {
            user.setCounter(new Counter(user.getId(), -1L));
        }
        if (increment) {
            user.getCounter().increment(allAttitudes.size() - 1);
        } else {
            user.getCounter().decrement(allAttitudes.size() - 1);
        }
        userRepository.save(user);
        return allAttitudes.get((int) user.getCounter().getCounter());
    }

    private boolean isPreference(User main, User candidate) {
        return (main.getPreference().equals(Constants.ALL.getName()) || main.getPreference().equals(candidate.getGender())) &&
                (candidate.getPreference().equals(Constants.ALL.getName()) || candidate.getPreference().equals(main.getGender()));
    }

}
