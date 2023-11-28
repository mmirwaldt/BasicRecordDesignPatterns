package net.mirwaldt.basic.records.design.patterns;

public class SealedTypes_01_Interfaces {
    sealed interface SealedInterface permits
            FinalSealedClass, SealedEnum, SealedRecord, NotSealedAnymoreInterface, NotSealedAnymoreClass,
            SealedSealedInterface/*, AbstractClass */ {

    }

    //----------------------------------- direct extension and implementations -----------------------------------------

    enum SealedEnum implements SealedInterface {

    }

    record SealedRecord(/*...*/) implements SealedInterface {

    }

    final class FinalSealedClass implements SealedInterface {

    }

    non-sealed interface NotSealedAnymoreInterface extends SealedInterface {

    }

    non-sealed class NotSealedAnymoreClass implements SealedInterface {

    }

    // Compiler error:
//    abstract class AbstractClass implements SealedInterface {
//
//    }

    //----------------------------------- direct extension and implementations -----------------------------------------



    //-------------------------------- sealed again extension and implementations --------------------------------------

    sealed interface SealedAgainInterface extends NotSealedAnymoreInterface permits NotSealedAnymoreAgainClass {

    }

    // This final class is only declared to avoid a compiler error with the declaration of the interface SealedAgainInterface
    final class NotSealedAnymoreAgainClass implements SealedAgainInterface {

    }

    //-------------------------------- sealed again extension and implementations --------------------------------------

    //--------------------------------double sealed extension and implementations --------------------------------------

    sealed interface SealedSealedInterface extends SealedInterface permits FinalSealedSealedInterfaceClass {

    }

    // This final class is only declared to avoid a compiler error with the declaration of the interface SealedSealedInterface
    final class FinalSealedSealedInterfaceClass implements SealedSealedInterface {

    }

    //--------------------------------double sealed extension and implementations --------------------------------------


    // This doesn't work:
//    sealed interface SealedInterfaceForRecords permits Record {
//
//    }
//
//    record SealedInterfaceRecord() implements SealedInterfaceForRecords {
//
//    }

    // This doesn't either work:
//    sealed interface SealedInterfaceForAbstractClasses permits AbstractClass {
//
//    }
//
//    abstract class AbstractClass  {
//
//    }
//
//    class SubClass extends AbstractClass implements SealedInterfaceForAbstractClasses {
//
//    }
}
