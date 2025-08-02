package ing.boykiss.gmtk25.input;

import com.badlogic.gdx.Gdx;
import ing.boykiss.gmtk25.GMTK25;
import ing.boykiss.gmtk25.event.Event;
import ing.boykiss.gmtk25.event.EventHandler;
import ing.boykiss.gmtk25.event.input.InputEvent;
import lombok.Getter;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class Input {
    public enum Keys {
        ANY_KEY(-1),
        UNKNOWN(0),
        SOFT_LEFT(1),
        META_SHIFT_ON(1),
        SOFT_RIGHT(2),
        META_ALT_ON(2),
        HOME(3),
        BACK(4),
        META_SYM_ON(4),
        CALL(5),
        ENDCALL(6),
        NUM_0(7),
        NUM_1(8),
        NUM_2(9),
        NUM_3(10),
        NUM_4(11),
        NUM_5(12),
        NUM_6(13),
        NUM_7(14),
        NUM_8(15),
        NUM_9(16),
        META_ALT_LEFT_ON(16),
        STAR(17),
        POUND(18),
        DPAD_UP(19),
        UP(19),
        DPAD_DOWN(20),
        DOWN(20),
        DPAD_LEFT(21),
        LEFT(21),
        DPAD_RIGHT(22),
        RIGHT(22),
        DPAD_CENTER(23),
        CENTER(23),
        VOLUME_UP(24),
        VOLUME_DOWN(25),
        POWER(26),
        CAMERA(27),
        CLEAR(28),
        A(29),
        B(30),
        C(31),
        D(32),
        META_ALT_RIGHT_ON(32),
        E(33),
        F(34),
        G(35),
        H(36),
        I(37),
        J(38),
        K(39),
        L(40),
        M(41),
        N(42),
        O(43),
        P(44),
        Q(45),
        R(46),
        S(47),
        T(48),
        U(49),
        V(50),
        W(51),
        X(52),
        Y(53),
        Z(54),
        COMMA(55),
        PERIOD(56),
        ALT_LEFT(57),
        ALT_RIGHT(58),
        SHIFT_LEFT(59),
        SHIFT_RIGHT(60),
        TAB(61),
        SPACE(62),
        SYM(63), // on MacOS, this is Command (⌘)
        EXPLORER(64),
        META_SHIFT_LEFT_ON(64),
        ENVELOPE(65),
        ENTER(66),
        DEL(67),
        BACKSPACE(67),
        GRAVE(68),
        MINUS(69),
        EQUALS(70),
        LEFT_BRACKET(71),
        RIGHT_BRACKET(72),
        BACKSLASH(73),
        SEMICOLON(74),
        APOSTROPHE(75),
        SLASH(76),
        AT(77),
        NUM(78),
        HEADSETHOOK(79),
        FOCUS(80),
        PLUS(81),
        MENU(82),
        NOTIFICATION(83),
        SEARCH(84),
        MEDIA_PLAY_PAUSE(85),
        MEDIA_STOP(86),
        MEDIA_NEXT(87),
        MEDIA_PREVIOUS(88),
        MEDIA_REWIND(89),
        MEDIA_FAST_FORWARD(90),
        MUTE(91),
        PAGE_UP(92),
        PAGE_DOWN(93),
        PICTSYMBOLS(94),
        SWITCH_CHARSET(95),
        BUTTON_A(96),
        BUTTON_B(97),
        BUTTON_C(98),
        BUTTON_X(99),
        BUTTON_Y(100),
        BUTTON_Z(101),
        BUTTON_L1(102),
        BUTTON_R1(103),
        BUTTON_L2(104),
        BUTTON_R2(105),
        BUTTON_THUMBL(106),
        BUTTON_THUMBR(107),
        BUTTON_START(108),
        BUTTON_SELECT(109),
        BUTTON_MODE(110),
        ESCAPE(111),
        FORWARD_DEL(112),
        CAPS_LOCK(115),
        SCROLL_LOCK(116),
        PRINT_SCREEN(120), // aka SYSRQ
        PAUSE(121), // aka break
        END(123),
        INSERT(124),
        META_SHIFT_RIGHT_ON(128),
        CONTROL_LEFT(129),
        CONTROL_RIGHT(130),
        F1(131),
        F2(132),
        F3(133),
        F4(134),
        F5(135),
        F6(136),
        F7(137),
        F8(138),
        F9(139),
        F10(140),
        F11(141),
        F12(142),
        NUM_LOCK(143),
        NUMPAD_0(144),
        NUMPAD_1(145),
        NUMPAD_2(146),
        NUMPAD_3(147),
        NUMPAD_4(148),
        NUMPAD_5(149),
        NUMPAD_6(150),
        NUMPAD_7(151),
        NUMPAD_8(152),
        NUMPAD_9(153),
        NUMPAD_DIVIDE(154),
        NUMPAD_MULTIPLY(155),
        NUMPAD_SUBTRACT(156),
        NUMPAD_ADD(157),
        NUMPAD_DOT(158),
        NUMPAD_COMMA(159),
        NUMPAD_ENTER(160),
        NUMPAD_EQUALS(161),
        NUMPAD_LEFT_PAREN(162),
        NUMPAD_RIGHT_PAREN(163),
        F13(183),
        F14(184),
        F15(185),
        F16(186),
        F17(187),
        F18(188),
        F19(189),
        F20(190),
        F21(191),
        F22(192),
        F23(193),
        F24(194),
        WORLD_1(240),
        WORLD_2(241),
        COLON(243),
        BUTTON_CIRCLE(255);

        Keys(int gdxKey) {
            this.gdxKey = gdxKey;
        }

        @Getter
        private final int gdxKey;
    }

    public static final List<Keys> PAUSE_KEYS = List.of(Keys.ESCAPE);

    private static final Map<Class<? extends Event>, EventHandler<?>> eventHandlers = Map.of(
        InputEvent.class, new EventHandler<InputEvent>()
    );

    private static final Set<Keys> keyStack = new HashSet<>();
    private static final Set<Keys> justPressedKeyStack = new HashSet<>();

    @SuppressWarnings({"unchecked"})
    public static <T extends Event> EventHandler<T> getEventHandler(Class<T> event) {
        return (EventHandler<T>) eventHandlers.get(event);
    }

    private static boolean lock = false;

    public static void lock() {
        lock = true;
    }

    public static void unlock() {
        lock = false;
    }

    public static void update() {
        //handle escape key separately so that pause works
        PAUSE_KEYS.forEach(Input::handleKeyUpdate);

        if (GMTK25.isPaused || lock) {
            if (isNotEmptyExceptPause(justPressedKeyStack)) clearExceptPause(justPressedKeyStack);
            if (isNotEmptyExceptPause(keyStack)) {
                filterExceptPause(keyStack).forEach(key -> getEventHandler(InputEvent.class).call(new InputEvent(key, true)));
                clearExceptPause(keyStack);
            }
            return;
        }

        for (Keys key : Keys.values()) {
            if (PAUSE_KEYS.contains(key)) continue;
            handleKeyUpdate(key);
        }
    }

    private static boolean isNotEmptyExceptPause(Set<Keys> set) {
        return !filterExceptPause(set).isEmpty();
    }

    private static void clearExceptPause(Set<Keys> set) {
        filterExceptPause(set).forEach(set::remove);
    }

    private static Set<Keys> filterExceptPause(Set<Keys> set) {
        return set.stream().filter(k -> !PAUSE_KEYS.contains(k)).collect(Collectors.toSet());
    }

    private static void handleKeyUpdate(Keys key) {
        if (Gdx.input.isKeyPressed(key.gdxKey)) {
            if (!justPressedKeyStack.contains(key)) {
                getEventHandler(InputEvent.class).call(new InputEvent(key, false));
                justPressedKeyStack.add(key);
            }
            keyStack.add(key);
        } else {
            if (justPressedKeyStack.contains(key)) {
                getEventHandler(InputEvent.class).call(new InputEvent(key, true));
                justPressedKeyStack.remove(key);
            }
            keyStack.remove(key);
        }
    }

    public static boolean keyPressed(Keys key) {
        return keyStack.contains(key);
    }
}
