package net.mirwaldt.basic.records.design.patterns;

public class SealedTypes_02_Classes {
    sealed class SealedSuperClass {

    }

    final class FinalSealedSubClass extends SealedSuperClass {

    }

    non-sealed class NotSealedAnymoreSubClass extends SealedSuperClass {

    }

    sealed class SealedAgainSubClass extends NotSealedAnymoreSubClass {

    }

    // This final class is only declared to avoid a compiler error with the declaration of the interface SealedAgainSubClass
    final class FinalSealedAgainSubClass extends SealedAgainSubClass {

    }

    sealed class SealedSealedSuperClass extends SealedSuperClass {

    }

    // This final class is only declared to avoid a compiler error with the declaration of the interface SealedSealedSuperClass
    final class SealedSealedSubClass extends SealedSealedSuperClass {

    }
}
